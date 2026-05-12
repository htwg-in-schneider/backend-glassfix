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

import de.htwg.in.schneider.glassfix.backend.model.Benutzer;
import de.htwg.in.schneider.glassfix.backend.repository.BenutzerRepository;

@RestController
@RequestMapping("/api/benutzer")
public class BenutzerController {
    private static final Logger LOG = LoggerFactory.getLogger(BenutzerController.class);

    @Autowired
    private BenutzerRepository benutzerRepository;

    @GetMapping
    public List<Benutzer> getAllBenutzer() {
        LOG.info("Fetching all benutzer");
        List<Benutzer> benutzer = benutzerRepository.findAll();
        LOG.info("Found {} benutzer", benutzer != null ? benutzer.size() : 0);
        return benutzer;
    }

    @PostMapping
    public Benutzer createBenutzer(@RequestBody Benutzer benutzer) {
        if (benutzer.getId() != null) {
            benutzer.setId(null);
            LOG.warn("Attempted to create an Benutzer with an existing ID. ID has been set to null to create a new benutzer.");
        }
        LOG.info("Creating new benutzer");
        Benutzer createdBenutzer = benutzerRepository.save(benutzer);
        LOG.info("Benutzer created with ID: {}", createdBenutzer.getId());
        return createdBenutzer;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Benutzer> getBenutzerById(@PathVariable Long id){
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
    public ResponseEntity<Void> deleteBenutzer(@PathVariable Long id) {
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
    public ResponseEntity<Benutzer> updateBenutzer(@PathVariable Long id, @RequestBody Benutzer benutzer) {
        LOG.info("Attempting to update benutzer with id {}", id);
        Optional<Benutzer> existingBenutzer = benutzerRepository.findById(id);
        if (!existingBenutzer.isPresent()) {
            LOG.warn("Benutzer with id {} not found for update", id);
            return ResponseEntity.notFound().build();
        }
        Benutzer benutzerToUpdate = existingBenutzer.get();
        benutzerToUpdate.setBenutzername(benutzer.getBenutzername());
        benutzerToUpdate.setEmail(benutzer.getEmail());
        benutzerToUpdate.setHashpasswort(benutzer.getHashpasswort());
        benutzerToUpdate.setRolle(benutzer.getRolle());
        benutzerToUpdate.setAdresse(benutzer.getAdresse());
        benutzerToUpdate.setTelefonnummer(benutzer.getTelefonnummer());
        Benutzer updatedBenutzer = benutzerRepository.save(benutzerToUpdate);
        LOG.info("Benutzer with id {} updated", id);

        return ResponseEntity.ok(updatedBenutzer);
    }

}