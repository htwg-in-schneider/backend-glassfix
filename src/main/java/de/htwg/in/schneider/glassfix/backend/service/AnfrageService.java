package de.htwg.in.schneider.glassfix.backend.service;


import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.oauth2.jwt.Jwt;

import jakarta.persistence.EntityNotFoundException;

import de.htwg.in.schneider.glassfix.backend.model.Anfrage;
import de.htwg.in.schneider.glassfix.backend.model.AnfrageStatus;
import de.htwg.in.schneider.glassfix.backend.model.Auskunft;
import de.htwg.in.schneider.glassfix.backend.model.AuskunftStatus;
import de.htwg.in.schneider.glassfix.backend.model.Benutzer;

import de.htwg.in.schneider.glassfix.backend.repository.AnfrageRepository;
import de.htwg.in.schneider.glassfix.backend.repository.AuskunftRepository;
import de.htwg.in.schneider.glassfix.backend.repository.BenutzerRepository;


@Service
@Transactional
public class AnfrageService {

    private static final Logger LOG = LoggerFactory.getLogger(AnfrageService.class);

    @Autowired
    private AnfrageRepository anfrageRepo;

    @Autowired
    private AuskunftRepository auskunftRepo;

    @Autowired
    private BenutzerRepository benutzerRepo;


    public Anfrage updateStatus(Anfrage anfrage, AnfrageStatus newStatus, Jwt jwt) {
        Optional<Benutzer> currentUser = benutzerRepo.findByOauthId(jwt.getSubject());
        if (!currentUser.isPresent()) {
            LOG.warn("No user found for JWT subject: " + jwt.getSubject());
            throw new EntityNotFoundException("User not found.");
        }
        Benutzer user = currentUser.get();


        if(newStatus.getOrder() != anfrage.getStatus().getOrder() + 1){
            LOG.warn("Attempted to set a status that doesen't correspond.");
            throw new IllegalStateException();
        }
        if(newStatus.getOrder() == 4){
            LOG.warn("Attempted to set a status AUSKUNFT_VORHANDEN which is set automatically.");
            throw new IllegalStateException();
        }


        if(newStatus.equals(AnfrageStatus.IN_PRUEFUNG)){
            Benutzer experte = benutzerRepo.findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Experte not found."));
            anfrage.setExperte(experte);
        }

        if(newStatus.equals(AnfrageStatus.PRUEFUNG_ABGESCHLOSSEN)){
            if(auskunftRepo.findByAnfrageId(anfrage.getId()) == null){
                Auskunft newAuskunft = new Auskunft();
                newAuskunft.setAnfrage(anfrage);
                newAuskunft.setStatus(AuskunftStatus.IN_BEARBEITUNG);
                auskunftRepo.save(newAuskunft);
            }
        }

        anfrage.setStatus(newStatus);
        return anfrage;
    }
}