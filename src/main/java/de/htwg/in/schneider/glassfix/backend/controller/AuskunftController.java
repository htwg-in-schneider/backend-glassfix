package de.htwg.in.schneider.glassfix.backend.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

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
                                                        @AuthenticationPrincipal Jwt jwt) {
        if(!sessionService.isLoggedIn(jwt)) {
            LOG.warn("Unauthenticated access attempt to GET /api/auskunft");
            return ResponseEntity.status(401).build();
        }

        
        if (sessionService.hasRole(jwt, Rolle.KUNDE)) {
            if (kundeId != null && !kundeId.equals(sessionService.getUserId(jwt))) {
                LOG.warn("KUNDE with id {} attempted to filter Auskunft by kundeId {} which does not match their own id. Ignoring kundeId filter.", 
                        sessionService.getUserId(jwt), 
                        kundeId);
            }
            if(status == AuskunftStatus.IN_BEARBEITUNG){
                LOG.info("User with id {} is a KUNDE and tried to filter by IN_BEARBEITUNG. Automatically filtering Auskunft by status: ANGEEBOT_VORHANDEN", sessionService.getUserId(jwt));
                status = AuskunftStatus.ANGEBOT_VORHANDEN;
            }
            kundeId = sessionService.getUserId(jwt);
             // Kunden dürfen nur offene Anfragen sehen
            LOG.info("User with id {} is a KUNDE. Automatically filtering Auskunft by kundeId: {}", kundeId, kundeId);
        }

        if (sessionService.hasRole(jwt, Rolle.FACHKRAFT)) {
            if (experteId != null && !experteId.equals(sessionService.getUserId(jwt))) {
                LOG.warn("FACHKRAFT with id {} attempted to filter Auskunft by experteId {} which does not match their own id. Ignoring experteId filter.", 
                        sessionService.getUserId(jwt),
                        experteId);
            }
            experteId = sessionService.getUserId(jwt);
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
    public ResponseEntity<Auskunft> getAuskunftByAnfrageId(@PathVariable Long anfrageId, @AuthenticationPrincipal Jwt jwt) {
        if(!sessionService.isLoggedIn(jwt)) {
            LOG.warn("Unauthenticated access attempt to GET /api/auskunft/anfrage/{}", anfrageId);
            return ResponseEntity.status(401).build();
        }
        Auskunft auskunft = auskunftRepository.findByAnfrageId(anfrageId);
        if (auskunft == null) {
            LOG.warn("Auskunft for Anfrage ID {} not found.", anfrageId);
            return ResponseEntity.notFound().build();
        }

        Anfrage anfrage = anfrageRepository.findById(anfrageId).orElse(null);

        if(sessionService.hasRole(jwt, Rolle.KUNDE)){
            if (anfrage == null || !anfrage.getKunde().getId().equals(sessionService.getUserId(jwt))) {
                LOG.warn("Kunde with ID {} attempted to access Auskunft for Anfrage ID {} that does not belong to them", 
                        sessionService.getUserId(jwt), 
                        anfrageId);
                return ResponseEntity.status(403).build();
            }
        }

        if(sessionService.hasRole(jwt, Rolle.FACHKRAFT)){
            
            if (anfrage == null || !anfrage.getExperte().getId().equals(sessionService.getUserId(jwt))) {
                LOG.warn("Fachkraft with ID {} attempted to access Auskunft for Anfrage ID {} that does not belong to them", sessionService.getUserId(jwt), anfrageId);
                return ResponseEntity.status(403).build();
            }
        }

        return ResponseEntity.ok(auskunft);
    }

    

    @PutMapping("/anfrage/{anfrageId}")
    public ResponseEntity<Auskunft> updateAuskunft(@PathVariable Long anfrageId, @RequestBody Auskunft updatedAuskunft, @AuthenticationPrincipal Jwt jwt) {
        if(!sessionService.isLoggedIn(jwt)) {
            LOG.warn("Unauthenticated access attempt to PUT /api/auskunft/anfrage/{}", anfrageId);
            return ResponseEntity.status(401).build();
        }
        Auskunft existingAuskunft = auskunftRepository.findByAnfrageId(anfrageId);
        if (existingAuskunft == null) {
            LOG.warn("Auskunft for Anfrage ID {} not found.", anfrageId);
            return ResponseEntity.status(404).build();
        }
        if(sessionService.hasRole(jwt, Rolle.FACHKRAFT)){
            if (!existingAuskunft.getAnfrage().getExperte().getId().equals(sessionService.getUserId(jwt))) {
                LOG.warn("Fachkraft with ID {} attempted to update Auskunft for Anfrage ID {} that does not belong to them", 
                        sessionService.getUserId(jwt),
                         anfrageId);
                return ResponseEntity.status(403).build();
            }
        }

        

        if (sessionService.hasRole(jwt, Rolle.FACHKRAFT) || sessionService.hasRole(jwt, Rolle.ADMIN) ) {
            if (existingAuskunft.getPreis() != null){
                LOG.warn("Fachkraft with ID {} attempted to update Auskunft with ID {}, which has already been added a price."
                        , sessionService.getUserId(jwt),
                         existingAuskunft.getId());
                return ResponseEntity.status(403).build();
            }
            if (updatedAuskunft.getReparaturEmpfehlung() != null) {
                existingAuskunft.setReparaturEmpfehlung(updatedAuskunft.getReparaturEmpfehlung());
            }
            if (updatedAuskunft.getZeitEinschaetzung() != null){
                existingAuskunft.setZeitEinschaetzung(updatedAuskunft.getZeitEinschaetzung());
            }
            if (updatedAuskunft.getArbeitsschritte() != null){
                existingAuskunft.setArbeitsschritte(updatedAuskunft.getArbeitsschritte());
            }
            if (updatedAuskunft.getVonExperteBearbeitet() != null && updatedAuskunft.getVonExperteBearbeitet()) {
                if (existingAuskunft.getVonExperteBearbeitet() != null && existingAuskunft.getVonExperteBearbeitet()) {
                    LOG.warn("Fachkraft tried to update Auskunft already sent to admin.");
                    return ResponseEntity.status(403).build();
                }
                existingAuskunft.setVonExperteBearbeitet(true);
            }
        }

        if (sessionService.hasRole(jwt, Rolle.GESCHAEFTSFUEHRER) || sessionService.hasRole(jwt, Rolle.ADMIN)){
            if (updatedAuskunft.getPreis() != null){
                existingAuskunft.setPreis(updatedAuskunft.getPreis());
            }
            if(updatedAuskunft.getStatus() != null && updatedAuskunft.getStatus() == AuskunftStatus.ANGEBOT_VORHANDEN){
                existingAuskunft.setStatus(updatedAuskunft.getStatus());
            }
            if(updatedAuskunft.getIstFreigegeben() != null && (existingAuskunft.getIstFreigegeben() == null || existingAuskunft.getIstFreigegeben() == false)){
                existingAuskunft.setIstFreigegeben(updatedAuskunft.getIstFreigegeben());
            }
            
        }

        if (sessionService.hasRole(jwt, Rolle.KUNDE) || sessionService.hasRole(jwt, Rolle.ADMIN)){
            if(updatedAuskunft.getStatus() != null && (updatedAuskunft.getStatus() == AuskunftStatus.ANGENOMMEN || updatedAuskunft.getStatus() == AuskunftStatus.ABGELEHNT)){
                existingAuskunft.setStatus(updatedAuskunft.getStatus());
            }
            
        }

        Auskunft auskunftSaved = auskunftRepository.save(existingAuskunft);
        LOG.info("Auskunft {} successfully updated.", auskunftSaved.getId());
        return ResponseEntity.ok(auskunftSaved);
    }

    @DeleteMapping("/anfrage/{anfrageId}")
    public ResponseEntity<Object> deleteAuskunft(@PathVariable Long anfrageId, @AuthenticationPrincipal  Jwt jwt){
        if(!sessionService.isLoggedIn(jwt)){
            LOG.warn("Unauthenticated access attempt to DELETE /api/auskunft/anfrage/{}", anfrageId);
            return ResponseEntity.status(401).build();
        }

        if(!sessionService.hasRole(jwt, Rolle.GESCHAEFTSFUEHRER) && !sessionService.hasRole(jwt, Rolle.ADMIN)){
            LOG.warn("User with ID {} and role {} attempted to delete Auskunft for Anfrage ID {}. Only GESCHAEFTSFUEHRER can delete Auskunft entries.", 
                    sessionService.getUserId(jwt), 
                    sessionService.hasRole(jwt, Rolle.KUNDE) ? "KUNDE" : sessionService.hasRole(jwt, Rolle.FACHKRAFT) ? "FACHKRAFT" : "UNKNOWN",
                    anfrageId);
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