package de.htwg.in.schneider.glassfix.backend.controller;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.htwg.in.schneider.glassfix.backend.model.Anfrage;
import de.htwg.in.schneider.glassfix.backend.repository.AnfrageRepository;

@RestController
@RequestMapping("/api/anfrage")
public class AnfrageController {

    @Autowired
    private AnfrageRepository anfrageRepository;

    @GetMapping
    public List<Anfrage> getAllAnfragen() {
        return anfrageRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<String> createAnfrage(@RequestBody Anfrage anfrage) {
        System.out.println("Controller called for anfrage: "+ anfrage.getId() + " - " + anfrage.getKategorie() + " - " 
            + anfrage.getKunde() + " - " + anfrage.getExperte() + " - " + anfrage.getStatus() + " - " 
            + anfrage.getErstellungsdatum() + " - " + anfrage.getBeschreibung() + " - " + anfrage.getFragen() + " - " 
            + anfrage.getBildUrl() + " - " + anfrage.getAntwort()    );
        return ResponseEntity.ok("POST successful");
    }
}