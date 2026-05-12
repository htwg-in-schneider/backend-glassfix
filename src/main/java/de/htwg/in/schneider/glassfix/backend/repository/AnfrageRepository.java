package de.htwg.in.schneider.glassfix.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import de.htwg.in.schneider.glassfix.backend.model.AnfrageStatus;

import de.htwg.in.schneider.glassfix.backend.model.Anfrage;
import java.util.List;

@Repository
public interface AnfrageRepository extends JpaRepository<Anfrage, Long> {
    List<Anfrage> findByKundeId(Long kundeId);
    List<Anfrage> findByExperteId(Long experteId);
    List<Anfrage> findByStatus(AnfrageStatus status);
    List<Anfrage> findByStatusAndKundeId(AnfrageStatus status, Long kundeId);
    List<Anfrage> findByStatusAndExperteId(AnfrageStatus status, Long experteId);
    List<Anfrage> findByKundeIdAndExperteId(Long kundeId, Long experteId);
    List<Anfrage> findByStatusAndKundeIdAndExperteId(AnfrageStatus status, Long kundeId, Long experteId);
}
