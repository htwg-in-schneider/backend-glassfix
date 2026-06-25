package de.htwg.in.schneider.glassfix.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.htwg.in.schneider.glassfix.backend.model.Kategorie;

@Repository
public interface KategorieRepository extends JpaRepository<Kategorie, Long> {

    Optional<Kategorie> findByNameIgnoreCase(String name);

    List<Kategorie> findByNameContainingIgnoreCaseOrBeschreibungContainingIgnoreCase(
            String name,
            String beschreibung
    );
}
