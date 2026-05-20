package de.htwg.in.schneider.glassfix.backend.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import de.htwg.in.schneider.glassfix.backend.model.Rolle;
import de.htwg.in.schneider.glassfix.backend.model.Anfrage;
import de.htwg.in.schneider.glassfix.backend.model.Auskunft;
import de.htwg.in.schneider.glassfix.backend.model.AuskunftStatus;
import de.htwg.in.schneider.glassfix.backend.repository.AnfrageRepository;
import de.htwg.in.schneider.glassfix.backend.repository.AuskunftRepository;
import de.htwg.in.schneider.glassfix.backend.service.ISessionService;
import jakarta.servlet.http.HttpSession;


@RestController
@RequestMapping("/api/auskunft")
public class AuskunftController {

    private static final Logger LOG = LoggerFactory.getLogger(AuskunftController.class);
    
    @Autowired
    private AuskunftRepository auskunftRepository;

    @Autowired
    private ISessionService sessionService;

    @Autowired
    private AnfrageRepository anfrageRepository;


    @GetMapping
    public ResponseEntity<List<Auskunft>> getAuskunft(@RequestParam(required = false) Long kundeId, 
                                                        @RequestParam(required = false) Long experteId, 
                                                        @RequestParam(required = false) AuskunftStatus status,
                                                        HttpSession session) {
        if(!sessionService.isLoggedIn(session)) {
            return ResponseEntity.status(401).build();
        }

        
        if (sessionService.hasRole(session, Rolle.KUNDE)) {
            if (kundeId != null && !kundeId.equals(sessionService.getUserId(session))) {
                LOG.warn("KUNDE with id {} attempted to filter Auskunft by kundeId {} which does not match their own id. Ignoring kundeId filter.", 
                        sessionService.getUserId(session), 
                        kundeId);
            }
            kundeId = sessionService.getUserId(session);
            LOG.info("User with id {} is a KUNDE. Automatically filtering Auskunft by kundeId: {}", kundeId, kundeId);
        }

        if (sessionService.hasRole(session, Rolle.FACHKRAFT)) {
            if (experteId != null && !experteId.equals(sessionService.getUserId(session))) {
                LOG.warn("FACHKRAFT with id {} attempted to filter Auskunft by experteId {} which does not match their own id. Ignoring experteId filter.", 
                        sessionService.getUserId(session),
                        experteId);
            }
            experteId = sessionService.getUserId(session);
            LOG.info("User with id {} is a FACHKRAFT. Automatically filtering Auskunft by experteId: {}", 
                experteId, experteId);
        }


        if(status == null && kundeId == null && experteId == null) {
            // No filters provided, return all Auskunft entries (Only for Geschäftsführer)
            LOG.info("No filters provided. Returning all Auskunft entries.");
            return ResponseEntity.ok(auskunftRepository.findAll());
        } 
        if(status != null && kundeId == null && experteId == null) {
            LOG.info("Filtering Auskunft entries by status: {}", status);
            return ResponseEntity.ok(auskunftRepository.findByStatus(status));
        }
        if(status == null && kundeId != null && experteId == null) {
            LOG.info("Filtering Auskunft entries by kundeId: {}", kundeId);
            return ResponseEntity.ok(auskunftRepository.findByAnfrageKundeId(kundeId));
        }
        if(status == null && kundeId == null && experteId != null) {
            LOG.info("Filtering Auskunft entries by experteId: {}", experteId);
            return ResponseEntity.ok(auskunftRepository.findByAnfrageExperteId(experteId));
        }
        if(status != null && kundeId != null && experteId == null) {
            LOG.info("Filtering Auskunft entries by status: {} and kundeId: {}", status, kundeId);
            return ResponseEntity.ok(auskunftRepository.findByStatusAndAnfrageKundeId(status, kundeId));
        }
        if(status != null && kundeId == null && experteId != null) {
            LOG.info("Filtering Auskunft entries by status: {} and experteId: {}", status, experteId);
            return ResponseEntity.ok(auskunftRepository.findByStatusAndAnfrageExperteId(status, experteId));
        }
        if(status == null && kundeId != null && experteId != null) {
            LOG.info("Filtering Auskunft entries by kundeId: {} and experteId: {}", kundeId, experteId);
            return ResponseEntity.ok(auskunftRepository.findByAnfrageKundeIdAndAnfrageExperteId(kundeId, experteId));
        }
        LOG.info("Filtering Auskunft entries by status: {}, kundeId: {} and experteId: {}", status, kundeId, experteId);
        return ResponseEntity.ok(auskunftRepository.findByStatusAndAnfrageKundeIdAndAnfrageExperteId(status, kundeId, experteId));
    }

    @GetMapping("/anfrage/{anfrageId}")
    public ResponseEntity<Auskunft> getAuskunftByAnfrageId(@PathVariable Long anfrageId, HttpSession session) {
        if(!sessionService.isLoggedIn(session)) {
            return ResponseEntity.status(401).build();
        }
        Auskunft auskunft = auskunftRepository.findByAnfrageId(anfrageId);
        if (auskunft == null) {
            return ResponseEntity.notFound().build();
        }

        Anfrage anfrage = anfrageRepository.findById(anfrageId).orElse(null);

        if(sessionService.hasRole(session, Rolle.KUNDE)){
            if (anfrage == null || !anfrage.getKunde().getId().equals(sessionService.getUserId(session))) {
                LOG.warn("Kunde with ID {} attempted to access Auskunft for Anfrage ID {} that does not belong to them", 
                        sessionService.getUserId(session), 
                        anfrageId);
                return ResponseEntity.status(403).build();
            }
        }

        if(sessionService.hasRole(session, Rolle.FACHKRAFT)){
            
            if (anfrage == null || !anfrage.getExperte().getId().equals(sessionService.getUserId(session))) {
                LOG.warn("Fachkraft with ID {} attempted to access Auskunft for Anfrage ID {} that does not belong to them", sessionService.getUserId(session), anfrageId);
                return ResponseEntity.status(403).build();
            }
        }

        return ResponseEntity.ok(auskunft);
    }

    

    @PutMapping("/anfrage/{anfrageId}")
    public ResponseEntity<Auskunft> updateAuskunft(@PathVariable Long anfrageId, @RequestBody Auskunft updatedAuskunft, HttpSession session) {
        if(!sessionService.isLoggedIn(session)) {
            return ResponseEntity.status(401).build();
        }
        if(sessionService.hasRole(session, Rolle.KUNDE)) {
            LOG.warn("Kunde with ID {} attempted to update Auskunft for Anfrage ID {}",
                     sessionService.getUserId(session),
                      anfrageId);
            return ResponseEntity.status(403).build();
        }
        Auskunft existingAuskunft = auskunftRepository.findByAnfrageId(anfrageId);
        if (existingAuskunft == null) {
            LOG.warn("Auskunft for Anfrage ID {} not found.", anfrageId);
            return ResponseEntity.status(404).build();
        }
        if(sessionService.hasRole(session, Rolle.FACHKRAFT)){
            if (!existingAuskunft.getAnfrage().getExperte().getId().equals(sessionService.getUserId(session))) {
                LOG.warn("Fachkraft with ID {} attempted to update Auskunft for Anfrage ID {} that does not belong to them", 
                        sessionService.getUserId(session),
                         anfrageId);
                return ResponseEntity.status(403).build();
            }
        }

        

        if (sessionService.hasRole(session, Rolle.FACHKRAFT) ) {
            if (existingAuskunft.getPreis() != null){
                LOG.warn("Fachkraft with ID {} attempted to update Auskunft with ID {}, which has already been added a price."
                        , sessionService.getUserId(session),
                         existingAuskunft.getId());
                return ResponseEntity.status(403).build();
            }
            if (updatedAuskunft.getZeitEinschaetzung() != null){
                existingAuskunft.setZeitEinschaetzung(updatedAuskunft.getZeitEinschaetzung());
            }
            if (updatedAuskunft.getArbeitsschritte() != null){
                existingAuskunft.setArbeitsschritte(updatedAuskunft.getArbeitsschritte());
            }
        }

        if (sessionService.hasRole(session, Rolle.GESCHAEFTSFUEHRER)){
            if (updatedAuskunft.getPreis() != null){
                existingAuskunft.setPreis(updatedAuskunft.getPreis());
            }
            
        }

        Auskunft auskunftSaved = auskunftRepository.save(existingAuskunft);
        LOG.info("Auskunft {} successfully updated.", auskunftSaved.getId());
        return ResponseEntity.ok(auskunftSaved);
    }

    @DeleteMapping("/anfrage/{anfrageId}")
    public ResponseEntity<Object> deleteAuskunft(@PathVariable Long anfrageId, HttpSession session){
        if(!sessionService.isLoggedIn(session)){
            return ResponseEntity.status(401).build();
        }

        if(!sessionService.hasRole(session, Rolle.GESCHAEFTSFUEHRER)){
            return ResponseEntity.status(403).build();
        }

        Auskunft toDelete = auskunftRepository.findByAnfrageId(anfrageId);
        if(toDelete == null){
            LOG.warn("Attempted to delete Auskunft with Anfrage ID {}, which doesn't exist.", anfrageId);
            return ResponseEntity.status(404).build();
        }

        auskunftRepository.delete(toDelete);
        LOG.info("Deleted Auskunft with Anfrage ID {}.", anfrageId);
        return ResponseEntity.noContent().build();

    }

}