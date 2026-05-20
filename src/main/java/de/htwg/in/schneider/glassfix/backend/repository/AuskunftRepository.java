package de.htwg.in.schneider.glassfix.backend.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import de.htwg.in.schneider.glassfix.backend.model.Auskunft;
import de.htwg.in.schneider.glassfix.backend.model.AuskunftStatus;
import java.util.List;


@Repository
public interface AuskunftRepository extends JpaRepository<Auskunft, Long> {
    Auskunft findByAnfrageId(Long anfrageId);
    List<Auskunft> findByStatus(AuskunftStatus status);
    List<Auskunft> findByIstFreigegeben(boolean istFreigegeben);
    List<Auskunft> findByAnfrageKundeId(Long kundeId);
    List<Auskunft> findByAnfrageExperteId(Long experteId);
    List<Auskunft> findByStatusAndAnfrageKundeId(AuskunftStatus status, Long kundeId);
    List<Auskunft> findByStatusAndAnfrageExperteId(AuskunftStatus status, Long experteId);
    List<Auskunft> findByAnfrageKundeIdAndAnfrageExperteId(Long kundeId, Long experteId);
    List<Auskunft> findByStatusAndAnfrageKundeIdAndAnfrageExperteId(AuskunftStatus status, Long kundeId, Long experteId);
}