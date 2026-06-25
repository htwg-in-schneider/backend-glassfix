package de.htwg.in.schneider.glassfix.backend.controller;

import java.util.List;
import java.util.Optional;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.htwg.in.schneider.glassfix.backend.model.Kategorie;
import de.htwg.in.schneider.glassfix.backend.model.Rolle;
import de.htwg.in.schneider.glassfix.backend.repository.KategorieRepository;
import de.htwg.in.schneider.glassfix.backend.service.ISessionService;

@RestController
@RequestMapping("/api/kategorien")
public class KategorieController {

    @Autowired
    private KategorieRepository kategorieRepository;

    @Autowired
    private ISessionService sessionService;

    @GetMapping
    public ResponseEntity<List<Kategorie>> getKategorien(
            @RequestParam(required = false) String suche
    ) {
        if (suche != null && !suche.isBlank()) {
            return ResponseEntity.ok(
                    kategorieRepository.findByNameContainingIgnoreCaseOrBeschreibungContainingIgnoreCase(
                            suche,
                            suche
                    )
            );
        }

        return ResponseEntity.ok(kategorieRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Kategorie> getKategorieById(@PathVariable Long id) {
        Optional<Kategorie> kategorie = kategorieRepository.findById(id);

        if (kategorie.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(kategorie.get());
    }

    @PostMapping
    public ResponseEntity<Kategorie> createKategorie(
            @RequestBody Kategorie kategorie,
            @AuthenticationPrincipal Jwt jwt
    ) {
        if (!istAdmin(jwt)) {
            return ResponseEntity.status(403).build();
        }

        if (kategorie == null || kategorie.getName() == null || kategorie.getName().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        if (kategorieRepository.findByNameIgnoreCase(kategorie.getName()).isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        kategorie.setId(null);
        Kategorie gespeicherteKategorie = kategorieRepository.save(kategorie);

        return ResponseEntity.status(201).body(gespeicherteKategorie);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Kategorie> updateKategorie(
            @PathVariable Long id,
            @RequestBody Kategorie kategorie,
            @AuthenticationPrincipal Jwt jwt
    ) {
        if (!istAdmin(jwt)) {
            return ResponseEntity.status(403).build();
        }

        Optional<Kategorie> vorhandeneKategorie = kategorieRepository.findById(id);

        if (vorhandeneKategorie.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (kategorie == null || kategorie.getName() == null || kategorie.getName().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        Kategorie kategorieToUpdate = vorhandeneKategorie.get();
        kategorieToUpdate.setName(kategorie.getName());
        kategorieToUpdate.setBeschreibung(kategorie.getBeschreibung());

        Kategorie aktualisierteKategorie = kategorieRepository.save(kategorieToUpdate);

        return ResponseEntity.ok(aktualisierteKategorie);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteKategorie(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        if (!istAdmin(jwt)) {
            return ResponseEntity.status(403).build();
        }

        if (!kategorieRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        kategorieRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    private boolean istAdmin(Jwt jwt) {
        return sessionService.isLoggedIn(jwt)
                && sessionService.hasRole(jwt, Rolle.ADMIN);
    }
}
