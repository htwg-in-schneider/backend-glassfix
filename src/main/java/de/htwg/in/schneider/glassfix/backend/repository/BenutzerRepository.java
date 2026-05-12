package de.htwg.in.schneider.glassfix.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.htwg.in.schneider.glassfix.backend.model.Benutzer;

@Repository
public interface BenutzerRepository extends JpaRepository<Benutzer, Long> {
    Benutzer findByEmail(String email);
    Benutzer findByBenutzername(String benutzername);
}