package de.htwg.in.schneider.glassfix.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.htwg.in.schneider.glassfix.backend.model.Anfrage;

@Repository
public interface AnfrageRepository extends JpaRepository<Anfrage, Long> {

}
