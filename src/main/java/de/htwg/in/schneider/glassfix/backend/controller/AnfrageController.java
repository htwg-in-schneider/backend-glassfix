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

import de.htwg.in.schneider.glassfix.backend.model.Anfrage;
import de.htwg.in.schneider.glassfix.backend.repository.AnfrageRepository;

@RestController
@RequestMapping("/api/anfrage")
public class AnfrageController {

    private static final Logger LOG = LoggerFactory.getLogger(AnfrageController.class);

    @Autowired
    private AnfrageRepository anfrageRepository;

    @GetMapping
    public List<Anfrage> getAllAnfragen() {
        return anfrageRepository.findAll();
    }

    @PostMapping
    public Anfrage createAnfrage(@RequestBody Anfrage anfrage) {
        if (anfrage.getId() != null) {
            anfrage.setId(null);
            LOG.warn("Attempted to create an anfrage with an existing ID. ID has been set to null to create a new anfrage.");
        }
        Anfrage newAnfrage = anfrageRepository.save(anfrage);
        LOG.info("Created new anfrage with id " + newAnfrage.getId());
        return newAnfrage;
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

    @PutMapping("/{id}")
    public ResponseEntity<Anfrage> updateAnfrage(@PathVariable Long id, @RequestBody Anfrage updatedAnfrage) {
        Optional<Anfrage> existingAnfrage = anfrageRepository.findById(id);
        if (!existingAnfrage.isPresent()) {
            LOG.warn("Attempted to update anfrage with id " + id + " but it was not found.");
            return ResponseEntity.notFound().build();
        }
        Anfrage anfrageToUpdate = existingAnfrage.get();
        
        anfrageToUpdate.setKategorie(updatedAnfrage.getKategorie());
        anfrageToUpdate.setKunde(updatedAnfrage.getKunde());
        anfrageToUpdate.setExperte(updatedAnfrage.getExperte());
        anfrageToUpdate.setStatus(updatedAnfrage.getStatus());
        anfrageToUpdate.setBeschreibung(updatedAnfrage.getBeschreibung());
        anfrageToUpdate.setFragen(updatedAnfrage.getFragen());
        anfrageToUpdate.setBildUrl(updatedAnfrage.getBildUrl());
        anfrageToUpdate.setAntwort(updatedAnfrage.getAntwort());

        Anfrage savedAnfrage = anfrageRepository.save(anfrageToUpdate);
        LOG.info("Updated anfrage with id " + savedAnfrage.getId());
        return ResponseEntity.ok(savedAnfrage);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteAnfrage(@PathVariable Long id) {
        Optional<Anfrage> anfrage = anfrageRepository.findById(id);
        if (!anfrage.isPresent()) {
            LOG.warn("Attempted to delete anfrage with id " + id + " but it was not found.");
            return ResponseEntity.notFound().build();
        } 
        anfrageRepository.deleteById(id);
        LOG.info("Deleted anfrage with id " + id);
        return ResponseEntity.noContent().build();
    }
}