package de.htwg.in.schneider.glassfix.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

import de.htwg.in.schneider.glassfix.backend.model.Benutzer;
import de.htwg.in.schneider.glassfix.backend.model.Rolle;
import java.util.List;

@Repository
public interface BenutzerRepository extends JpaRepository<Benutzer, Long> {
    Benutzer findByEmail(String email);
    Optional<Benutzer> findByOauthId(String oauthId);
    List<Benutzer> findByRolle(Rolle rolle);
}