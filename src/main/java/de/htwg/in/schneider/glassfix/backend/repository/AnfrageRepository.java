package de.htwg.in.schneider.glassfix.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.htwg.in.schneider.glassfix.backend.model.Anfrage;
import java.util.List;

@Repository
public interface AnfrageRepository extends JpaRepository<Anfrage, Long> {
    List<Anfrage> findByKundeId(Long kundeId);
    List<Anfrage> findByExperteId(Long experteId);
}
