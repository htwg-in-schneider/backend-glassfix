package de.htwg.in.schneider.glassfix.backend.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpSession;

import de.htwg.in.schneider.glassfix.backend.model.Benutzer;
import de.htwg.in.schneider.glassfix.backend.repository.BenutzerRepository;
import de.htwg.in.schneider.glassfix.backend.model.Rolle;
import de.htwg.in.schneider.glassfix.backend.service.ISessionService;

@RestController
@RequestMapping("/api/benutzer")
public class BenutzerController {
    private static final Logger LOG = LoggerFactory.getLogger(BenutzerController.class);

    @Autowired
    private BenutzerRepository benutzerRepository;

    @Autowired
    private ISessionService sessionService;

    @GetMapping
    public ResponseEntity<List<Benutzer>> getAllBenutzer(HttpSession session) {
        if (!sessionService.isLoggedIn(session)) {
            LOG.warn("Unauthorized attempt to fetch all benutzer. User is not logged in.");
            return ResponseEntity.status(401).build();
        }
        if (!sessionService.hasRole(session, Rolle.GESCHAEFTSFUEHRER)){
            LOG.warn("Unauthorized attempt to fetch all Benutzer. Only Geschaeftsfuehrer is allowed to fetch all Benutzer.");
            return ResponseEntity.status(403).build();
        }
        LOG.info("Fetching all benutzer");
        List<Benutzer> benutzer = benutzerRepository.findAll();
        LOG.info("Found {} benutzer", benutzer != null ? benutzer.size() : 0);
        return ResponseEntity.ok(benutzer);
    }

    @PostMapping
    public ResponseEntity<Benutzer> createBenutzer(@RequestBody Benutzer benutzer, HttpSession session) {
        if (!sessionService.isLoggedIn(session) || !sessionService.hasRole(session, Rolle.GESCHAEFTSFUEHRER)) {
            benutzer.setRolle(Rolle.KUNDE);
        }
        if (benutzer.getId() != null) {
            benutzer.setId(null);
            LOG.warn("Attempted to create an Benutzer with an existing ID. ID has been set to null to create a new benutzer.");
        }
        LOG.info("Creating new benutzer");
        Benutzer createdBenutzer = benutzerRepository.save(benutzer);
        LOG.info("Benutzer created with ID: {}", createdBenutzer.getId());
        return ResponseEntity.status(201).body(createdBenutzer);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Benutzer> getBenutzerById(@PathVariable Long id, HttpSession session) {
        if (!sessionService.isLoggedIn(session)) {
            LOG.warn("Unauthorized attempt to fetch benutzer with id {}. User is not logged in.", id);
            return ResponseEntity.status(401).build();
        }
        if (!id.equals(sessionService.getUserId(session))) {
            LOG.warn("Benutzer with id {} attempted to fetch benutzer with id {} which does not match their own id. Ignoring request.", sessionService.getUserId(session), id);
            return ResponseEntity.status(403).build();
        }
        LOG.info("Fetching benutzer with id {}", id);
        Optional<Benutzer> benutzer = benutzerRepository.findById(id);
        if (benutzer.isPresent()) {
            LOG.info("Benutzer with id {} found", id);
            return ResponseEntity.ok(benutzer.get());
        } else {
            LOG.warn("Benutzer with id {} not found", id);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBenutzer(@PathVariable Long id, HttpSession session) {
        if (!sessionService.isLoggedIn(session)) {
            LOG.warn("Unauthorized attempt to delete benutzer with id {}. User is not logged in.", id);
            return ResponseEntity.status(401).build();
        }
        if (!id.equals(sessionService.getUserId(session))) {
            LOG.warn("Benutzer with id {} attempted to delete benutzer with id {} which does not match their own id. Ignoring request.", sessionService.getUserId(session), id);
            return ResponseEntity.status(403).build();
        }
        LOG.info("Attempting to delete benutzer with id {}", id);
        if (!benutzerRepository.existsById(id)) {
            LOG.warn("Benutzer with id {} not found for deletion", id);
            return ResponseEntity.notFound().build();
        }
        benutzerRepository.deleteById(id);
        LOG.info("Benutzer with id {} deleted", id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Benutzer> updateBenutzer(@PathVariable Long id, @RequestBody Benutzer benutzer, HttpSession session) {
        if (!sessionService.isLoggedIn(session)) {
            LOG.warn("Unauthorized attempt to update benutzer with id {}. User is not logged in.", id);
            return ResponseEntity.status(401).build();
        }
        if (!id.equals(sessionService.getUserId(session))) {
            LOG.warn("Benutzer with id {} attempted to update benutzer with id {} which does not match their own id. Ignoring request.", sessionService.getUserId(session), id);
            return ResponseEntity.status(403).build();
        }
        LOG.info("Attempting to update benutzer with id {}", id);
        Optional<Benutzer> existingBenutzer = benutzerRepository.findById(id);
        if (!existingBenutzer.isPresent()) {
            LOG.warn("Benutzer with id {} not found for update", id);
            return ResponseEntity.notFound().build();
        }
        Benutzer benutzerToUpdate = existingBenutzer.get();
        
        if(benutzer.getBenutzername() != null) {
            benutzerToUpdate.setBenutzername(benutzer.getBenutzername());
        }
        if(benutzer.getEmail() != null) {
            benutzerToUpdate.setEmail(benutzer.getEmail());
        }
        if(benutzer.getHashpasswort() != null) {
            benutzerToUpdate.setHashpasswort(benutzer.getHashpasswort());
        }
        if(benutzer.getAdresse() != null) {
            benutzerToUpdate.setAdresse(benutzer.getAdresse());
        }
        if(benutzer.getTelefonnummer() != null) {
            benutzerToUpdate.setTelefonnummer(benutzer.getTelefonnummer());
        }

        Benutzer updatedBenutzer = benutzerRepository.save(benutzerToUpdate);
        LOG.info("Benutzer with id {} updated", id);

        return ResponseEntity.ok(updatedBenutzer);
    }

}