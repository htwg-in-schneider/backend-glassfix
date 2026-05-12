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

import de.htwg.in.schneider.glassfix.backend.model.Rolle;
import de.htwg.in.schneider.glassfix.backend.model.Benutzer;
import de.htwg.in.schneider.glassfix.backend.model.Anfrage;
import de.htwg.in.schneider.glassfix.backend.repository.AnfrageRepository;
import de.htwg.in.schneider.glassfix.backend.repository.BenutzerRepository;

@RestController
@RequestMapping("/api/anfrage")
public class AnfrageController {

    private static final Logger LOG = LoggerFactory.getLogger(AnfrageController.class);

    @Autowired
    private AnfrageRepository anfrageRepository;

    @Autowired
    private BenutzerRepository benutzerRepository;

    @GetMapping
    public List<Anfrage> getAllAnfragen() {
        LOG.info("Fetching all anfragen");
        List<Anfrage> anfragen = anfrageRepository.findAll();
        LOG.info("Found {} anfragen", anfragen != null ? anfragen.size() : 0);
        return anfragen;
    }

    @PostMapping
    public ResponseEntity<Anfrage> createAnfrage(@RequestBody Anfrage anfrage) {

        Long kundeId = null;
        if (anfrage != null && anfrage.getKunde() != null) {
            kundeId = anfrage.getKunde().getId();
        }
        LOG.info("Attempting to create new anfrage for kunde with id " + kundeId);

        if (anfrage == null) {
            LOG.warn("Received null anfrage object in request body. Cannot create anfrage.");
            return ResponseEntity.badRequest().build();
        }

        if (anfrage.getKunde() == null || anfrage.getKunde().getId() == null) {
            LOG.warn("Anfrage object is missing kunde information. Cannot create anfrage.");
            return ResponseEntity.badRequest().build();
        }

        Benutzer kunde = benutzerRepository.findById(anfrage.getKunde().getId()).orElse(null);
        if (kunde == null) {
            LOG.warn("No kunde found with id " + anfrage.getKunde().getId() + ". Cannot create anfrage.");
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
    public List<Anfrage> getAnfragenByKundeId(@PathVariable Long kundeId) {
        LOG.info("Fetching anfragen for kunde with id " + kundeId);
        List<Anfrage> anfragen = anfrageRepository.findByKundeId(kundeId);
        LOG.info("Found {} anfragen for kunde with id {}", anfragen != null ? anfragen.size() : 0, kundeId);
        return anfragen;
    }

    @GetMapping("/experte/{experteId}")
    public List<Anfrage> getAnfragenByExperteId(@PathVariable Long experteId) {
        LOG.info("Fetching anfragen for experte with id " + experteId);
        List<Anfrage> anfragen = anfrageRepository.findByExperteId(experteId);
        LOG.info("Found {} anfragen for experte with id {}", anfragen != null ? anfragen.size() : 0, experteId);
        return anfragen;
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