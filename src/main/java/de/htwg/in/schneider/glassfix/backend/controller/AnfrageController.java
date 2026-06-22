package de.htwg.in.schneider.glassfix.backend.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import de.htwg.in.schneider.glassfix.backend.model.Rolle;
import de.htwg.in.schneider.glassfix.backend.model.Benutzer;
import de.htwg.in.schneider.glassfix.backend.model.Anfrage;
import de.htwg.in.schneider.glassfix.backend.model.AnfrageStatus;
import de.htwg.in.schneider.glassfix.backend.repository.AnfrageRepository;
import de.htwg.in.schneider.glassfix.backend.repository.BenutzerRepository;
import de.htwg.in.schneider.glassfix.backend.service.ISessionService;
import de.htwg.in.schneider.glassfix.backend.service.AnfrageService;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/anfrage")
public class AnfrageController {

    private static final Logger LOG = LoggerFactory.getLogger(AnfrageController.class);

    @Autowired
    private AnfrageRepository anfrageRepository;

    @Autowired
    private BenutzerRepository benutzerRepository;

    @Autowired
    private ISessionService sessionService;

    @Autowired
    private AnfrageService anfrageService;


    @GetMapping
    public ResponseEntity<List<Anfrage>> getAnfragen(@RequestParam(required = false) AnfrageStatus status, 
                                        @RequestParam(required = false) Long kundeId, 
                                        @RequestParam(required = false) Long experteId, @AuthenticationPrincipal Jwt jwt) {
        
        if (!sessionService.isLoggedIn(jwt)) {
            LOG.warn("Unauthorized attempt to fetch anfragen. User is not logged in.");
            return ResponseEntity.status(401).build();
        }

        if (sessionService.hasRole(jwt, Rolle.KUNDE)) {
            if (kundeId != null && !kundeId.equals(sessionService.getUserId(jwt))) {
                LOG.warn("KUNDE with id {} attempted to filter anfragen by kundeId {} which does not match their own id. Ignoring kundeId filter.", sessionService.getUserId(jwt), kundeId);
            }
            kundeId = sessionService.getUserId(jwt);
            LOG.info("User with id {} is a KUNDE. Automatically filtering anfragen by kundeId: {}", kundeId, kundeId);
        }

        if (sessionService.hasRole(jwt, Rolle.FACHKRAFT)) {
            if (experteId != null && !experteId.equals(sessionService.getUserId(jwt))) {
                LOG.warn("FACHKRAFT with id {} attempted to filter anfragen by experteId {} which does not match their own id. Ignoring filter.", sessionService.getUserId(jwt), experteId);
                experteId = sessionService.getUserId(jwt);
            }
        }


        if(status == null && kundeId == null && experteId == null) {
            LOG.info("Fetching all anfragen without filters.");
            return ResponseEntity.ok(anfrageRepository.findAll());
        } 
        else if(status != null && kundeId == null && experteId == null) {
            LOG.info("Fetching anfragen filtered by status: {}", status);
            return ResponseEntity.ok(anfrageRepository.findByStatus(status));
        }
        else if(status == null && kundeId != null && experteId == null) {
            LOG.info("Fetching anfragen filtered by kundeId: {}", kundeId);
            return ResponseEntity.ok(anfrageRepository.findByKundeId(kundeId));
        }
        else if(status == null && kundeId == null && experteId != null) {   
            LOG.info("Fetching anfragen filtered by experteId: {}", experteId);
            return ResponseEntity.ok(anfrageRepository.findByExperteId(experteId));
        }
        else if(status != null && kundeId != null && experteId == null) {
            LOG.info("Fetching anfragen filtered by status: {} and kundeId: {}", status, kundeId);
            return ResponseEntity.ok(anfrageRepository.findByStatusAndKundeId(status, kundeId));
        }
        else if(status != null && kundeId == null && experteId != null) {
            LOG.info("Fetching anfragen filtered by status: {} and experteId: {}", status, experteId);
            return ResponseEntity.ok(anfrageRepository.findByStatusAndExperteId(status, experteId));
        }
        else if(status == null && kundeId != null && experteId != null) {
            LOG.info("Fetching anfragen filtered by kundeId: {} and experteId: {}", kundeId, experteId);
            return ResponseEntity.ok(anfrageRepository.findByKundeIdAndExperteId(kundeId, experteId));
        }
        LOG.info("Fetching anfragen filtered by status: {}, kundeId: {} and experteId: {}", status, kundeId, experteId);
        return ResponseEntity.ok(anfrageRepository.findByStatusAndKundeIdAndExperteId(status, kundeId, experteId));

    }

    @PostMapping
    public ResponseEntity<Anfrage> createAnfrage(@RequestBody Anfrage anfrage, @AuthenticationPrincipal Jwt jwt) {

        if (!sessionService.isLoggedIn(jwt) || !sessionService.hasRole(jwt, Rolle.KUNDE)) {
            LOG.warn("Unauthorized attempt to create anfrage. User is not logged in or does not have the required role.");
            return ResponseEntity.status(401).build();
        }

        Long kundeId = sessionService.getUserId(jwt);
        Benutzer kunde = benutzerRepository.findById(kundeId).orElse(null);
        if (kunde == null) {
            LOG.warn("No kunde found with id " + kundeId + " from session. Cannot create anfrage.");
            return ResponseEntity.status(404).build();
        }

        LOG.info("Attempting to create new anfrage for kunde with id " + kundeId);

        if (anfrage == null) {
            LOG.warn("Received null anfrage object in request body. Cannot create anfrage.");
            return ResponseEntity.badRequest().build();
        }

        anfrage.setExperte(null);
        anfrage.setStatus(AnfrageStatus.ERSTELLT);
        anfrage.setKunde(kunde);
        Anfrage createdAnfrage = anfrageRepository.save(anfrage);
        LOG.info("Anfrage created with id " + createdAnfrage.getId() + " for kunde with id " + kundeId);
        return ResponseEntity.ok(createdAnfrage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Anfrage> getAnfrageById(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt){
        if (!sessionService.isLoggedIn(jwt)){
            return ResponseEntity.status(401).build();
        }
        Optional<Anfrage> anfrage = anfrageRepository.findById(id);
        if (anfrage.isPresent()) {
            if (sessionService.hasRole(jwt, Rolle.KUNDE)){
                if(!anfrage.get().getKunde().getId().equals(sessionService.getUserId(jwt))){
                    LOG.warn("Kunde with ID {} attempted to see an Anfrage not of its own.", sessionService.getUserId(jwt));
                    return ResponseEntity.status(403).build();
                }
            }
            if(sessionService.hasRole(jwt, Rolle.FACHKRAFT)){
                if(anfrage.get().getExperte() != null && !anfrage.get().getExperte().getId().equals(sessionService.getUserId(jwt))){
                    LOG.warn("Fachkraft with ID {} attemted to see an Anfrage he is not part of.", sessionService.getUserId(jwt));
                    return ResponseEntity.status(403).build();
                }
            }
            return ResponseEntity.ok(anfrage.get());
        } else {
            LOG.warn("Anfrage with id " + id + " not found.");
            return ResponseEntity.notFound().build();
        }
    }

    

   @PutMapping("/{id}")
    public ResponseEntity<Anfrage> updateAnfrage(@PathVariable Long id, @RequestBody Anfrage updatedAnfrage, @AuthenticationPrincipal Jwt jwt) {
    Optional<Anfrage> existingAnfrage = anfrageRepository.findById(id);
    
        if (!existingAnfrage.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        if (!sessionService.isLoggedIn(jwt)) {
            return ResponseEntity.status(401).build();
        }

        Anfrage anfrageToUpdate = existingAnfrage.get();

        try {
            if (sessionService.hasRole(jwt, Rolle.KUNDE)) {
                // Kunden dürfen nur eigene Anfragen bearbeiten, und nur bestimmte Felder (Kategorie, Beschreibung, BildUrl, Fragen)
                if (!anfrageToUpdate.getKunde().getId().equals(sessionService.getUserId(jwt))) {
                    return ResponseEntity.status(403).build();
                }

                if(!anfrageToUpdate.getStatus().equals(AnfrageStatus.ERSTELLT)){
                    LOG.warn("Kunde with ID {} tried to update Anfrage, which is no longer available for updates.", sessionService.getUserId(jwt));
                    return ResponseEntity.status(403).build();
                }
                
                if (updatedAnfrage.getKategorie() != null) anfrageToUpdate.setKategorie(updatedAnfrage.getKategorie());
                if (updatedAnfrage.getBeschreibung() != null) anfrageToUpdate.setBeschreibung(updatedAnfrage.getBeschreibung());
                if (updatedAnfrage.getBildUrl() != null) anfrageToUpdate.setBildUrl(updatedAnfrage.getBildUrl());
                if (updatedAnfrage.getFragen() != null) anfrageToUpdate.setFragen(updatedAnfrage.getFragen());
            }
 
            if (sessionService.hasRole(jwt, Rolle.FACHKRAFT)) {
                // Eigene Anfragen dürfen nur FACHKRAFT bearbeiten, und nur bestimmte Felder (Antwort)
                    if (anfrageToUpdate.getExperte() != null && !anfrageToUpdate.getExperte().getId().equals(sessionService.getUserId(jwt))) {
                        return ResponseEntity.status(403).build();
                    }
                if (updatedAnfrage.getAntwort() != null) {
                    anfrageToUpdate.setAntwort(updatedAnfrage.getAntwort());
                }
                if (updatedAnfrage.getStatus() != null && updatedAnfrage.getStatus() != anfrageToUpdate.getStatus()) {
                    anfrageService.updateStatus(anfrageToUpdate, updatedAnfrage.getStatus(), jwt);
                }
            }
            

            Anfrage savedAnfrage = anfrageRepository.save(anfrageToUpdate);
            LOG.info("Anfrage {} successfully updated.", savedAnfrage.getId());
            return ResponseEntity.ok(savedAnfrage);

        } catch (IllegalStateException e) {
            // Wenn setExperte eine IllegalStateException wirft, bedeutet das, dass bereits ein Experte zugewiesen ist.
            // In diesem Fall geben wir eine 400 Bad Request zurück, da der Client versucht, einen ungültigen Zustand zu erreichen.
            LOG.warn("Conflict when assigning expert: {}", e.getMessage());
            return ResponseEntity.badRequest().header("X-Error-Cause", e.getMessage()).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteAnfrage(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        if (!sessionService.isLoggedIn(jwt)) {
            return ResponseEntity.status(401).build();
        }

        if(sessionService.hasRole(jwt, Rolle.FACHKRAFT)){
            return ResponseEntity.status(403).build();
        }

        Optional<Anfrage> anfrage = anfrageRepository.findById(id);
        if (!anfrage.isPresent()) {
            LOG.warn("Attempted to delete anfrage with id " + id + " but it was not found.");
            return ResponseEntity.notFound().build();
        } 

        if (sessionService.hasRole(jwt, Rolle.KUNDE) && !anfrage.get().getKunde().getId().equals(sessionService.getUserId(jwt))) {
            return ResponseEntity.status(403).build();
        }

        
        anfrageRepository.deleteById(id);
        LOG.info("Deleted anfrage with id " + id);
        return ResponseEntity.noContent().build();
    }
}