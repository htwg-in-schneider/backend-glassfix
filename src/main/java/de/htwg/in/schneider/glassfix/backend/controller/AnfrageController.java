package de.htwg.in.schneider.glassfix.backend.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import de.htwg.in.schneider.glassfix.backend.model.Rolle;
import de.htwg.in.schneider.glassfix.backend.model.Benutzer;
import de.htwg.in.schneider.glassfix.backend.model.Anfrage;
import de.htwg.in.schneider.glassfix.backend.model.AnfrageStatus;
import de.htwg.in.schneider.glassfix.backend.repository.AnfrageRepository;
import de.htwg.in.schneider.glassfix.backend.repository.BenutzerRepository;
import de.htwg.in.schneider.glassfix.backend.service.ISessionService;
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

    @GetMapping
    public List<Anfrage> getAnfragen(@RequestParam(required = false) AnfrageStatus status, 
                                        @RequestParam(required = false) Long kundeId, 
                                        @RequestParam(required = false) Long experteId, HttpSession session) {
        
        if (!sessionService.isLoggedIn(session)) {
            LOG.warn("Unauthorized attempt to fetch anfragen. User is not logged in.");
            return List.of();
        }

        if (sessionService.hasRole(session, Rolle.KUNDE)) {
            if (kundeId != null && !kundeId.equals(sessionService.getUserId(session))) {
                LOG.warn("KUNDE with id {} attempted to filter anfragen by kundeId {} which does not match their own id. Ignoring kundeId filter.", sessionService.getUserId(session), kundeId);
            }
            kundeId = sessionService.getUserId(session);
            LOG.info("User with id {} is a KUNDE. Automatically filtering anfragen by kundeId: {}", kundeId, kundeId);
        }

        if (sessionService.hasRole(session, Rolle.FACHKRAFT)) {
            if (experteId != null && !experteId.equals(sessionService.getUserId(session))) {
                LOG.warn("FACHKRAFT with id {} attempted to filter anfragen by experteId {} which does not match their own id. Ignoring experteId filter.", sessionService.getUserId(session), experteId);
            }
            experteId = sessionService.getUserId(session);
            LOG.info("User with id {} is a FACHKRAFT. Automatically filtering anfragen by experteId: {}", experteId, experteId);
        }


        if(status == null && kundeId == null && experteId == null) {
            LOG.info("Fetching all anfragen without filters.");
            return anfrageRepository.findAll();
        } 
        else if(status != null && kundeId == null && experteId == null) {
            LOG.info("Fetching anfragen filtered by status: {}", status);
            return anfrageRepository.findByStatus(status);
        }
        else if(status == null && kundeId != null && experteId == null) {
            LOG.info("Fetching anfragen filtered by kundeId: {}", kundeId);
            return anfrageRepository.findByKundeId(kundeId);
        }
        else if(status == null && kundeId == null && experteId != null) {   
            LOG.info("Fetching anfragen filtered by experteId: {}", experteId);
            return anfrageRepository.findByExperteId(experteId);
        }
        else if(status != null && kundeId != null && experteId == null) {
            LOG.info("Fetching anfragen filtered by status: {} and kundeId: {}", status, kundeId);
            return anfrageRepository.findByStatusAndKundeId(status, kundeId);
        }
        else if(status != null && kundeId == null && experteId != null) {
            LOG.info("Fetching anfragen filtered by status: {} and experteId: {}", status, experteId);
            return anfrageRepository.findByStatusAndExperteId(status, experteId);
        }
        else if(status == null && kundeId != null && experteId != null) {
            LOG.info("Fetching anfragen filtered by kundeId: {} and experteId: {}", kundeId, experteId);
            return anfrageRepository.findByKundeIdAndExperteId(kundeId, experteId);
        }
        LOG.info("Fetching anfragen filtered by status: {}, kundeId: {} and experteId: {}", status, kundeId, experteId);
        return anfrageRepository.findByStatusAndKundeIdAndExperteId(status, kundeId, experteId);

    }

    @PostMapping
    public ResponseEntity<Anfrage> createAnfrage(@RequestBody Anfrage anfrage, HttpSession session) {

        if (!sessionService.isLoggedIn(session) || !sessionService.hasRole(session, Rolle.KUNDE)) {
            LOG.warn("Unauthorized attempt to create anfrage. User is not logged in or does not have the required role.");
            return ResponseEntity.status(401).build();
        }

        Long kundeId = sessionService.getUserId(session);
        Benutzer kunde = benutzerRepository.findById(kundeId).orElse(null);
        if (kunde == null) {
            LOG.warn("No kunde found with id " + kundeId + " from session. Cannot create anfrage.");
            return ResponseEntity.status(401).build();
        }

        LOG.info("Attempting to create new anfrage for kunde with id " + kundeId);

        if (anfrage == null) {
            LOG.warn("Received null anfrage object in request body. Cannot create anfrage.");
            return ResponseEntity.badRequest().build();
        }

        anfrage.setKunde(kunde);
        Anfrage createdAnfrage = anfrageRepository.save(anfrage);
        LOG.info("Anfrage created with id " + createdAnfrage.getId() + " for kunde with id " + kundeId);
        return ResponseEntity.ok(createdAnfrage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Anfrage> getAnfrageById(@PathVariable Long id){
        Optional<Anfrage> anfrage = anfrageRepository.findById(id);
        if (anfrage.isPresent()) {
            return ResponseEntity.ok(anfrage.get());
        } else {
            LOG.warn("Anfrage with id " + id + " not found.");
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/kunde/{kundeId}")
    public ResponseEntity<List<Anfrage>> getAnfragenByKundeId(@PathVariable Long kundeId, HttpSession session) {
        if (!sessionService.isLoggedIn(session)) {
            LOG.warn("Unauthorized attempt to fetch anfragen for kunde with id " + kundeId + ". User is not logged in.");
            return ResponseEntity.status(401).build();
        }
        if (sessionService.hasRole(session, Rolle.KUNDE) && !kundeId.equals(sessionService.getUserId(session))) {
            LOG.warn("KUNDE with id {} attempted to fetch anfragen for kundeId {} which does not match their own id. Ignoring request.", sessionService.getUserId(session), kundeId);
            return ResponseEntity.status(403).build();
        }
        LOG.info("Fetching anfragen for kunde with id " + kundeId);
        List<Anfrage> anfragen = anfrageRepository.findByKundeId(kundeId);
        LOG.info("Found {} anfragen for kunde with id {}", anfragen != null ? anfragen.size() : 0, kundeId);
        return ResponseEntity.ok(anfragen);
    }

    @GetMapping("/experte/{experteId}")
    public ResponseEntity<List<Anfrage>> getAnfragenByExperteId(@PathVariable Long experteId, HttpSession session) {
        if (!sessionService.isLoggedIn(session)) {
            LOG.warn("Unauthorized attempt to fetch anfragen for experte with id " + experteId + ". User is not logged in.");
            return ResponseEntity.status(401).build();
        }
        if (sessionService.hasRole(session, Rolle.FACHKRAFT) && !experteId.equals(sessionService.getUserId(session))) {
            LOG.warn("FACHKRAFT with id {} attempted to fetch anfragen for experteId {} which does not match their own id. Ignoring request.", sessionService.getUserId(session), experteId);
            return ResponseEntity.status(403).build();
        }
        LOG.info("Fetching anfragen for experte with id " + experteId);
        List<Anfrage> anfragen = anfrageRepository.findByExperteId(experteId);
        LOG.info("Found {} anfragen for experte with id {}", anfragen != null ? anfragen.size() : 0, experteId);
        return ResponseEntity.ok(anfragen);
    }   

   @PutMapping("/{id}")
    public ResponseEntity<Anfrage> updateAnfrage(@PathVariable Long id, @RequestBody Anfrage updatedAnfrage, HttpSession session) {
    Optional<Anfrage> existingAnfrage = anfrageRepository.findById(id);
    
        if (!existingAnfrage.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        if (!sessionService.isLoggedIn(session)) {
            return ResponseEntity.status(401).build();
        }

        Anfrage anfrageToUpdate = existingAnfrage.get();

        try {
            if (sessionService.hasRole(session, Rolle.KUNDE)) {
                // Eigene Anfragen dürfen nur KUNDE bearbeiten, und nur bestimmte Felder (Kategorie, Beschreibung, BildUrl, Fragen)
                if (!anfrageToUpdate.getKunde().getId().equals(sessionService.getUserId(session))) {
                    return ResponseEntity.status(403).build();
                }
                anfrageToUpdate.setKategorie(updatedAnfrage.getKategorie());    
                anfrageToUpdate.setBeschreibung(updatedAnfrage.getBeschreibung());
                anfrageToUpdate.setBildUrl(updatedAnfrage.getBildUrl());
                anfrageToUpdate.setFragen(updatedAnfrage.getFragen());
            }

            // 
            if (sessionService.hasRole(session, Rolle.FACHKRAFT)) {
                // Eigene Anfragen dürfen nur FACHKRAFT bearbeiten, und nur bestimmte Felder (Antwort)
                    if (anfrageToUpdate.getExperte() == null || !anfrageToUpdate.getExperte().getId().equals(sessionService.getUserId(session))) {
                        return ResponseEntity.status(403).build();
                    }
                anfrageToUpdate.setAntwort(updatedAnfrage.getAntwort());
            }
            
            // Nur GESCHAEFTSFUEHRER können den Experten zuweisen, und das ändert automatisch den Status auf IN_PRUEFUNG
            if (sessionService.hasRole(session, Rolle.GESCHAEFTSFUEHRER)) {
                if (updatedAnfrage.getExperte() != null) {
                    // Wenn setExperte eine IllegalStateException wirft, wird sie in der catch-Klausel weiter unten abgefangen und behandelt
                    anfrageToUpdate.setExperte(updatedAnfrage.getExperte());
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
    public ResponseEntity<Object> deleteAnfrage(@PathVariable Long id, HttpSession session) {
        if (!sessionService.isLoggedIn(session)) {
            return ResponseEntity.status(401).build();
        }

        if(sessionService.hasRole(session, Rolle.FACHKRAFT)){
            return ResponseEntity.status(403).build();
        }

        Optional<Anfrage> anfrage = anfrageRepository.findById(id);
        if (!anfrage.isPresent()) {
            LOG.warn("Attempted to delete anfrage with id " + id + " but it was not found.");
            return ResponseEntity.notFound().build();
        } 

        if (sessionService.hasRole(session, Rolle.KUNDE) && !anfrage.get().getKunde().getId().equals(sessionService.getUserId(session))) {
            return ResponseEntity.status(403).build();
        }

        
        anfrageRepository.deleteById(id);
        LOG.info("Deleted anfrage with id " + id);
        return ResponseEntity.noContent().build();
    }
}