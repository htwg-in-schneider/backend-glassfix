package de.htwg.in.schneider.glassfix.backend.service;


import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpSession;

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

    @Autowired
    private ISessionService sessionService;

    public Anfrage updateStatus(Anfrage anfrage, AnfrageStatus newStatus, HttpSession session){

        if(newStatus.getOrder() != anfrage.getStatus().getOrder() + 1){
            LOG.warn("Attempted to set a status that doesen't correspond.");
            throw new IllegalStateException();
        }
        if(newStatus.getOrder() == 4){
            LOG.warn("Attempted to set a status AUSKUNFT_VORHANDEN which is set automatically.");
            throw new IllegalStateException();
        }


        if(newStatus.equals(AnfrageStatus.IN_PRUEFUNG)){
            Benutzer experte = benutzerRepo.findById(sessionService.getUserId(session))
                .orElseThrow(() -> new EntityNotFoundException("Experte not found. Problem with the session."));
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