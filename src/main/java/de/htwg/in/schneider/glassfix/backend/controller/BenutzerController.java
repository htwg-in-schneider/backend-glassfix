package de.htwg.in.schneider.glassfix.backend.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.htwg.in.schneider.glassfix.backend.model.Benutzer;
import de.htwg.in.schneider.glassfix.backend.model.Rolle;
import de.htwg.in.schneider.glassfix.backend.repository.BenutzerRepository;
import de.htwg.in.schneider.glassfix.backend.service.ISessionService;

@RestController
@RequestMapping("/api/benutzer")
public class BenutzerController {

    private static final Logger LOG = LoggerFactory.getLogger(BenutzerController.class);

    @Autowired
    private BenutzerRepository benutzerRepository;

    @Autowired
    private ISessionService sessionService;

    private boolean istAdmin(Jwt jwt) {
        return sessionService.isLoggedIn(jwt)
                && sessionService.hasRole(jwt, Rolle.ADMIN);
    }

    private boolean darfBenutzerLesenOderBearbeiten(Jwt jwt, Long benutzerId) {
        Long eigeneId = sessionService.getUserId(jwt);
        boolean istEigenerBenutzer = eigeneId != null && eigeneId.equals(benutzerId);
        return istEigenerBenutzer || istAdmin(jwt);
    }

    @GetMapping
    public ResponseEntity<List<Benutzer>> getAllBenutzer(@AuthenticationPrincipal Jwt jwt) {
        if (!sessionService.isLoggedIn(jwt)) {
            LOG.warn("Unauthorized attempt to fetch all Benutzer. User is not logged in.");
            return ResponseEntity.status(401).build();
        }

        if (!istAdmin(jwt)) {
            LOG.warn("Unauthorized attempt to fetch all Benutzer. Only ADMIN is allowed.");
            return ResponseEntity.status(403).build();
        }

        LOG.info("Fetching all Benutzer");
        return ResponseEntity.ok(benutzerRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Benutzer> createBenutzer(
            @RequestBody Benutzer benutzer,
            @AuthenticationPrincipal Jwt jwt
    ) {
        if (!sessionService.isLoggedIn(jwt)) {
            LOG.warn("Unauthorized attempt to create Benutzer. User is not logged in.");
            return ResponseEntity.status(401).build();
        }

        if (!istAdmin(jwt)) {
            LOG.warn("Unauthorized attempt to create Benutzer. Only ADMIN is allowed.");
            return ResponseEntity.status(403).build();
        }

        if (benutzer == null) {
            return ResponseEntity.badRequest().build();
        }

        if (benutzer.getName() == null || benutzer.getName().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        if (benutzer.getEmail() == null || benutzer.getEmail().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        if (benutzer.getRolle() == null) {
            benutzer.setRolle(Rolle.KUNDE);
        }

        benutzer.setId(null);

        Benutzer createdBenutzer = benutzerRepository.save(benutzer);
        LOG.info("Benutzer created with ID: {}", createdBenutzer.getId());

        return ResponseEntity.status(201).body(createdBenutzer);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Benutzer> getBenutzerById(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        if (!sessionService.isLoggedIn(jwt)) {
            LOG.warn("Unauthorized attempt to fetch Benutzer with id {}. User is not logged in.", id);
            return ResponseEntity.status(401).build();
        }

        if (!darfBenutzerLesenOderBearbeiten(jwt, id)) {
            LOG.warn("Benutzer with id {} attempted to fetch Benutzer with id {} without permission.",
                    sessionService.getUserId(jwt), id);
            return ResponseEntity.status(403).build();
        }

        Optional<Benutzer> benutzer = benutzerRepository.findById(id);

        if (benutzer.isEmpty()) {
            LOG.warn("Benutzer with id {} not found", id);
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(benutzer.get());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Benutzer> updateBenutzer(
            @PathVariable Long id,
            @RequestBody Benutzer benutzer,
            @AuthenticationPrincipal Jwt jwt
    ) {
        if (!sessionService.isLoggedIn(jwt)) {
            LOG.warn("Unauthorized attempt to update Benutzer with id {}. User is not logged in.", id);
            return ResponseEntity.status(401).build();
        }

        if (!darfBenutzerLesenOderBearbeiten(jwt, id)) {
            LOG.warn("Benutzer with id {} attempted to update Benutzer with id {} without permission.",
                    sessionService.getUserId(jwt), id);
            return ResponseEntity.status(403).build();
        }

        if (benutzer == null) {
            return ResponseEntity.badRequest().build();
        }

        if (benutzer.getName() != null && benutzer.getName().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        Optional<Benutzer> existingBenutzer = benutzerRepository.findById(id);

        if (existingBenutzer.isEmpty()) {
            LOG.warn("Benutzer with id {} not found for update", id);
            return ResponseEntity.notFound().build();
        }

        Benutzer benutzerToUpdate = existingBenutzer.get();

        if (benutzer.getName() != null) {
            benutzerToUpdate.setName(benutzer.getName());
        }

        if (benutzer.getAdresse() != null) {
            benutzerToUpdate.setAdresse(benutzer.getAdresse());
        }

        if (benutzer.getTelefonnummer() != null) {
            benutzerToUpdate.setTelefonnummer(benutzer.getTelefonnummer());
        }

        if (benutzer.getRolle() != null && istAdmin(jwt)) {
            benutzerToUpdate.setRolle(benutzer.getRolle());
        }

        Benutzer updatedBenutzer = benutzerRepository.save(benutzerToUpdate);
        LOG.info("Benutzer with id {} updated", id);

        return ResponseEntity.ok(updatedBenutzer);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBenutzer(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        if (!sessionService.isLoggedIn(jwt)) {
            LOG.warn("Unauthorized attempt to delete Benutzer with id {}. User is not logged in.", id);
            return ResponseEntity.status(401).build();
        }

        if (!istAdmin(jwt)) {
            LOG.warn("Unauthorized attempt to delete Benutzer with id {}. Only ADMIN is allowed.", id);
            return ResponseEntity.status(403).build();
        }

        if (!benutzerRepository.existsById(id)) {
            LOG.warn("Benutzer with id {} not found for deletion", id);
            return ResponseEntity.notFound().build();
        }

        benutzerRepository.deleteById(id);
        LOG.info("Benutzer with id {} deleted", id);

        return ResponseEntity.noContent().build();
    }
}
