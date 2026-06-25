package de.htwg.in.schneider.glassfix.backend.config;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.htwg.in.schneider.glassfix.backend.model.Anfrage;
import de.htwg.in.schneider.glassfix.backend.model.Benutzer;
import de.htwg.in.schneider.glassfix.backend.model.Rolle;
import de.htwg.in.schneider.glassfix.backend.model.Kategorie;
import de.htwg.in.schneider.glassfix.backend.repository.AnfrageRepository;
import de.htwg.in.schneider.glassfix.backend.repository.BenutzerRepository;
import de.htwg.in.schneider.glassfix.backend.repository.KategorieRepository;

@Configuration
public class DataLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataLoader.class);

    @Bean
    public CommandLineRunner loadData(AnfrageRepository anfrageRepository, BenutzerRepository benutzerRepository, KategorieRepository kategorieRepository) {
        return args -> {
            loadInitialBenutzer(benutzerRepository);
            loadInitialData(anfrageRepository, benutzerRepository);
            loadInitialKategorien(kategorieRepository);
        };
    }

    private void loadInitialBenutzer(BenutzerRepository benutzerRepository) {
        if(benutzerRepository.findByOauthId("auth0|6a3c77723f066f10a68e55aa").isEmpty()){
            Benutzer juanADMIN = new Benutzer();
            juanADMIN.setOauthId("auth0|6a3c77723f066f10a68e55aa");
            juanADMIN.setRolle(Rolle.ADMIN);
            juanADMIN.setName("Juan ADMIN");
            juanADMIN.setEmail("ja+admin@gmail.com");
            benutzerRepository.save(juanADMIN);
        }

        if (benutzerRepository.findByOauthId("auth0|6a355e8047250c6aa65d405a").isEmpty()) {
            Benutzer juanKunde = new Benutzer();
            juanKunde.setOauthId("auth0|6a355e8047250c6aa65d405a");
            juanKunde.setRolle(Rolle.KUNDE);
            juanKunde.setName("Juan Kunde");
            juanKunde.setEmail("juancerda+kunde@gmail.com");
            benutzerRepository.save(juanKunde);
        }
        if (benutzerRepository.findByOauthId("auth0|6a355eb28fe62975fa6f9abf").isEmpty()) {
            Benutzer juanExperte = new Benutzer();
            juanExperte.setOauthId("auth0|6a355eb28fe62975fa6f9abf");
            juanExperte.setRolle(Rolle.FACHKRAFT);
            juanExperte.setName("Juan Experte");
            juanExperte.setEmail("juancerda+fachkraft@gmail.com");
            benutzerRepository.save(juanExperte);
        }
        if(benutzerRepository.findByOauthId("auth0|6a355f3f47250c6aa65d4109").isEmpty()) {
            Benutzer juanAdmin = new Benutzer();
            juanAdmin.setOauthId("auth0|6a355f3f47250c6aa65d4109");
            juanAdmin.setRolle(Rolle.GESCHAEFTSFUEHRER);
            juanAdmin.setName("Juan GF");
            juanAdmin.setEmail("juancerda+admin@gmail.com");
            benutzerRepository.save(juanAdmin);
        }
        if(benutzerRepository.findByOauthId("auth0|6a3560c147250c6aa65d42b7").isEmpty()) {
            Benutzer romanKunde = new Benutzer();
            romanKunde.setOauthId("auth0|6a3560c147250c6aa65d42b7");
            romanKunde.setRolle(Rolle.KUNDE);
            romanKunde.setName("Roman Kunde");
            romanKunde.setEmail("romanmueller+kunde@gmail.com");
            benutzerRepository.save(romanKunde);
        }
        if(benutzerRepository.findByOauthId("auth0|6a35614be52d13d7d3e5a5eb").isEmpty()) {
            Benutzer romanExperte = new Benutzer();
            romanExperte.setOauthId("auth0|6a35614be52d13d7d3e5a5eb");
            romanExperte.setRolle(Rolle.FACHKRAFT);
            romanExperte.setName("Roman Experte");
            romanExperte.setEmail("romanmueller+fachkraft@gmail.com");
            benutzerRepository.save(romanExperte);
        }
        if(benutzerRepository.findByOauthId("auth0|6a3561c463741e31d64a4a87").isEmpty()) {
            Benutzer romanAdmin = new Benutzer();
            romanAdmin.setOauthId("auth0|6a3561c463741e31d64a4a87");
            romanAdmin.setRolle(Rolle.GESCHAEFTSFUEHRER);
            romanAdmin.setName("Roman GF");
            romanAdmin.setEmail("romanmueller+admin@gmail.com");
            benutzerRepository.save(romanAdmin);
        }
    }

    private void loadInitialKategorien(KategorieRepository kategorieRepository) {
        if (kategorieRepository.count() == 0) {
            kategorieRepository.save(new Kategorie("Trinkglas", "Schäden an Trinkgläsern, Kratzern oder Glasrändern"));
            kategorieRepository.save(new Kategorie("Fensterglas", "Schäden an Fenstern oder Scheibenglas"));
            kategorieRepository.save(new Kategorie("Vase", "Schäden an Glasvasen"));
            kategorieRepository.save(new Kategorie("Spiegel", "Schäden an Spiegelglas"));
        }
    }

    private void loadInitialData(AnfrageRepository anfrageRepository, BenutzerRepository benutzerRepository) {
        if (anfrageRepository.count() == 0) {
            // Anfrage Roman Kunde
            Benutzer kunde = benutzerRepository.findByOauthId("auth0|6a3560c147250c6aa65d42b7").orElseThrow();
            Anfrage anfrage1 = new Anfrage();
            anfrage1.setKunde(kunde);
            anfrage1.setBeschreibung("Fenster ist kaputt");
            anfrage1.setKategorie("Fensterglas");
            anfrage1.setFragen("Wie ist die Größe des Fensters? Welche Art von Glas ist es? Gibt es weitere Schäden am Fenster?");
            anfrage1.setBildUrl("/AuftragBspBilder/VaseKratzer.png");
            anfrageRepository.save(anfrage1);

            // Anfrage Juan Kunde
            Benutzer kunde2 = benutzerRepository.findByOauthId("auth0|6a355e8047250c6aa65d405a").orElseThrow();
            Anfrage anfrage2 = new Anfrage();
            anfrage2.setKunde(kunde2);
            anfrage2.setBeschreibung("Spiegel ist gesprungen");
            anfrage2.setKategorie("Spiegel");
            anfrage2.setFragen("Wie groß ist der Spiegel? Welche Art von Glas ist es? Gibt es weitere Schäden am Spiegel?");
            anfrage2.setBildUrl("/AuftragBspBilder/VaseRand.png");
            anfrageRepository.save(anfrage2);
        }
    }
}
