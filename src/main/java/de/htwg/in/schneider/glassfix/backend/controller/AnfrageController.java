package de.htwg.in.schneider.glassfix.backend.controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;


import java.util.List;

@RestController
@RequestMapping("/api/anfrage")
public class AnfrageController {

    public static class Anfrage {
        private int id;
        private String kunde;
        private String beschreibung;
        private String BildUrl; // URL zum Bild der Anfrage

        public Anfrage(int id, String description, String BildUrl, String kunde) {
            this.id = id;
            this.beschreibung = description;
            this.BildUrl = BildUrl;
            this.kunde = kunde;
        }

        public int getId() {
            return id;
        }

        public String getBeschreibung() {
            return beschreibung;
        }
        public String getBildUrl() {
            return BildUrl;
        }
        public String getKunde() {
            return kunde;
        }

    }

    @GetMapping
    public List<Anfrage> getAllAnfragen() {
        return List.of(
                new Anfrage(1, "Anfrage 1", "https://example.com/image1.jpg", "Kunde 1"),
                new Anfrage(2, "Anfrage 2", "https://example.com/image2.jpg", "Kunde 2")
        );
    }

    @PostMapping
    public ResponseEntity<String> createAnfrage(@RequestBody Anfrage anfrage) {
        System.out.println("Controller called for anfrage: "+ anfrage.getId() + " - " + 
            anfrage.getBeschreibung() + " - " + anfrage.getBildUrl() + " - " + anfrage.getKunde());
        return ResponseEntity.ok("POST successful");
    }
}