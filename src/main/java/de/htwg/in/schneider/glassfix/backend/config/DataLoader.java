package de.htwg.in.schneider.glassfix.backend.config;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.htwg.in.schneider.glassfix.backend.model.Anfrage;
import de.htwg.in.schneider.glassfix.backend.repository.AnfrageRepository;

@Configuration
public class DataLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataLoader.class);

    @Bean
    public CommandLineRunner loadData(AnfrageRepository repository) {
        return args -> {
            if (repository.count() == 0) { // Check if the repository is empty
                LOGGER.info("Database is empty. Loading initial data...");
                loadInitialData(repository);
            } else {
                LOGGER.info("Database already contains data. Skipping data loading.");
            }
        };
    }

    private void loadInitialData(AnfrageRepository repository) {
        Anfrage anfrage1 = new Anfrage();
        anfrage1.setKategorie("Kategorie 1");
        anfrage1.setKunde("Kunde 1");
        anfrage1.setBeschreibung("Beschreibung 1");
        anfrage1.setFragen("Fragen 1");
        anfrage1.setBildUrl("https://neshanjo.github.io/saitenweise-images/violin_pro.jpg");

        Anfrage anfrage2 = new Anfrage();
        anfrage2.setKategorie("Kategorie 2");
        anfrage2.setKunde("Kunde 2");
        anfrage2.setBeschreibung("Beschreibung 2");
        anfrage2.setFragen("Fragen 2");
        anfrage2.setBildUrl("https://neshanjo.github.io/saitenweise-images/doublebass_pro.jpg");

        repository.saveAll(Arrays.asList(anfrage1, anfrage2));
        LOGGER.info("Initial data loaded successfully.");
    }
}
