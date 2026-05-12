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
    public CommandLineRunner loadData(AnfrageRepository repository, BenutzerRepository benutzerRepository) {
        return args -> {
            if (repository.count() == 0) { // Check if the repository is empty
                LOGGER.info("Database is empty. Loading initial data...");
                loadInitialData(repository, benutzerRepository);
            } else {
                LOGGER.info("Database already contains data. Skipping data loading.");
            }
        };
    }

    private void loadInitialData(AnfrageRepository repository, BenutzerRepository benutzerRepository) {
        Benutzer kunde1 = new Benutzer();
        kunde1.setBenutzername("Kunde1");
        kunde1.setEmail("kunde1@example.com");
        kunde1.setHashpasswort("passwort1");
        kunde1.setRolle(Rolle.KUNDE);
        kunde1.setAdresse("Musterstraße 1, 12345 Musterstadt");
        kunde1.setTelefonnummer("0123456789");
        benutzerRepository.save(kunde1);
        benutzerRepository.save(kunde1);

        Benutzer kunde2 = new Benutzer();
        kunde2.setBenutzername("Kunde2");
        kunde2.setEmail("kunde2@example.com");
        kunde2.setHashpasswort("passwort2");
        kunde2.setRolle(Rolle.KUNDE);
        kunde2.setAdresse("Beispielweg 2, 54321 Beispielstadt");
        kunde2.setTelefonnummer("0987654321");
        benutzerRepository.save(kunde2);

        Anfrage anfrage1 = new Anfrage();
        anfrage1.setKategorie("Kategorie 1");
        anfrage1.setKunde(kunde1);
        anfrage1.setBeschreibung("Beschreibung 1");
        anfrage1.setFragen("Fragen 1");
        anfrage1.setBildUrl("https://neshanjo.github.io/saitenweise-images/violin_pro.jpg");

        Anfrage anfrage2 = new Anfrage();
        anfrage2.setKategorie("Kategorie 2");
        anfrage2.setKunde(kunde2);
        anfrage2.setBeschreibung("Beschreibung 2");
        anfrage2.setFragen("Fragen 2");
        anfrage2.setBildUrl("https://neshanjo.github.io/saitenweise-images/doublebass_pro.jpg");

        repository.saveAll(Arrays.asList(anfrage1, anfrage2));
        LOGGER.info("Initial data loaded successfully.");
    }
}
