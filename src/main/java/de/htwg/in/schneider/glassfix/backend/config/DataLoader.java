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
import de.htwg.in.schneider.glassfix.backend.repository.AnfrageRepository;
import de.htwg.in.schneider.glassfix.backend.repository.BenutzerRepository;

@Configuration
public class DataLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataLoader.class);

    @Bean
    public CommandLineRunner loadData(AnfrageRepository anfrageRepository, BenutzerRepository benutzerRepository) {
        return args -> {
            if (anfrageRepository.count() == 0) { // Check if the repository is empty
                LOGGER.info("Database is empty. Loading initial data...");
                loadInitialData(anfrageRepository, benutzerRepository);
            } else {
                LOGGER.info("Database already contains data. Skipping data loading.");
            }
        };
    }

    private void loadInitialData(AnfrageRepository anfrageRepository, BenutzerRepository benutzerRepository) {
        Benutzer experte1 = new Benutzer();
        experte1.setBenutzername("Experte_1");
        experte1.setEmail("experte1@example.com");
        experte1.setHashpasswort("expertepasswort1");
        experte1.setRolle(Rolle.FACHKRAFT);
        experte1.setAdresse("Expertenstraße 1, 12345 Expertenstadt");
        experte1.setTelefonnummer("0123456789");
        benutzerRepository.save(experte1);

        Benutzer experte2 = new Benutzer();
        experte2.setBenutzername("Experte_2");
        experte2.setEmail("experte2@example.com");
        experte2.setHashpasswort("expertepasswort2");
        experte2.setRolle(Rolle.FACHKRAFT);
        experte2.setAdresse("Expertenstraße 2, 12345 Expertenstadt");
        experte2.setTelefonnummer("0123456789");
        benutzerRepository.save(experte2);


        Benutzer roman = new Benutzer();
        roman.setBenutzername("Roman_Mueller");
        roman.setEmail("roman.mueller@example.com");
        roman.setHashpasswort("passwort123");
        roman.setRolle(Rolle.KUNDE);
        roman.setAdresse("Musterstraße 1, 12345 Musterstadt");
        roman.setTelefonnummer("0123456789");
        benutzerRepository.save(roman);

        Benutzer juan = new Benutzer();
        juan.setBenutzername("Juan_Cerda");
        juan.setEmail("juan.cerda@example.com");
        juan.setHashpasswort("passwort123");
        juan.setRolle(Rolle.KUNDE);
        juan.setAdresse("Beispielweg 2, 54321 Beispielstadt");
        juan.setTelefonnummer("0987654321");
        benutzerRepository.save(juan);

        Benutzer max = new Benutzer();
        max.setBenutzername("Max_Mustermann");
        max.setEmail("max.mustermann@example.com");
        max.setHashpasswort("passwort3");
        max.setRolle(Rolle.KUNDE);
        max.setAdresse("Musterstraße 3, 12345 Musterstadt");
        max.setTelefonnummer("0123456789");
        benutzerRepository.save(max);

        Benutzer maike = new Benutzer();
        maike.setBenutzername("Maike_Meier");
        maike.setEmail("meike.meier@example.com");
        maike.setHashpasswort("passwort4");
        maike.setRolle(Rolle.KUNDE);
        maike.setAdresse("Musterstraße 4, 12345 Musterstadt");
        maike.setTelefonnummer("0123456789");
        benutzerRepository.save(maike);

        Anfrage anfrageRoman = new Anfrage();
        anfrageRoman.setKategorie("Kategorie 1");
        anfrageRoman.setKunde(roman);
        anfrageRoman.setBeschreibung("Beschreibung 1");
        anfrageRoman.setFragen("Fragen 1");
        anfrageRoman.setBildUrl("https://localhost:5173/frontend-glassfix/ProjektBilder/BeforeAfterLinks.png");

        Anfrage anfrageJuan = new Anfrage();
        anfrageJuan.setKategorie("Kategorie 2");
        anfrageJuan.setKunde(juan);
        anfrageJuan.setBeschreibung("Beschreibung 2");
        anfrageJuan.setFragen("Fragen 2");
        anfrageJuan.setBildUrl("https://localhost:5173/frontend-glassfix/ProjektBilder/BeforeAfterLinks.png");

        Anfrage anfrageMax = new Anfrage();
        anfrageMax.setKategorie("Kategorie 3");
        anfrageMax.setKunde(max);
        anfrageMax.setBeschreibung("Beschreibung 3");
        anfrageMax.setFragen("Fragen 3");
        anfrageMax.setBildUrl("https://localhost:5173/frontend-glassfix/ProjektBilder/BeforeAfterLinks.png");

        Anfrage anfrageMaike = new Anfrage();
        anfrageMaike.setKategorie("Kategorie 4");
        anfrageMaike.setKunde(maike);
        anfrageMaike.setBeschreibung("Beschreibung 4");
        anfrageMaike.setFragen("Fragen 4");
        anfrageMaike.setBildUrl("https://localhost:5173/frontend-glassfix/ProjektBilder/BeforeAfterLinks.png");

        anfrageRepository.saveAll(Arrays.asList(anfrageRoman, anfrageJuan, anfrageMax, anfrageMaike));
        LOGGER.info("Initial data loaded successfully.");
    }
}
