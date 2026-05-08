package de.htwg.in.schneider.glassfix.backend.controller;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.htwg.in.schneider.glassfix.backend.model.Anfrage;

@RestController
@RequestMapping("/api/anfrage")
public class AnfrageController {

    @GetMapping
    public List<Anfrage> getAllAnfragen() {
       Anfrage anfrage1 = new Anfrage();
        anfrage1.setId(1);
        anfrage1.setKategorie("A");
        anfrage1.setKunde("Max Mustermann");
        anfrage1.setBeschreibung("Mein Glas ist kaputt.");
        anfrage1.setFragen("Wie kann ich das Problem beheben? Was kostet die Reparatur? Wie lange dauert die Reparatur?");
        anfrage1.setBildUrl("https://example.com/images/smartphone.jpg");
    
        Anfrage anfrage2 = new Anfrage();
        anfrage2.setId(2);
        anfrage2.setKategorie("B");
        anfrage2.setKunde("Erika Musterfrau");
        anfrage2.setBeschreibung("Mein Glas Objekt ist beschädigt.");
        anfrage2.setFragen("Wie kann ich das Problem beheben? Was kostet die Reparatur? Wie lange dauert die Reparatur?");
        anfrage2.setBildUrl("https://example.com/images/laptop.jpg");

        return List.of(anfrage1, anfrage2);
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