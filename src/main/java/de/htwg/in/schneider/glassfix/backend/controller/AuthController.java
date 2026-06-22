package de.htwg.in.schneider.glassfix.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import de.htwg.in.schneider.glassfix.backend.model.Benutzer;
import de.htwg.in.schneider.glassfix.backend.repository.BenutzerRepository;
import de.htwg.in.schneider.glassfix.backend.service.SessionService;
import de.htwg.in.schneider.glassfix.backend.service.ISessionService;
import java.util.Optional;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;



@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private BenutzerRepository benutzerRepository;

    @Autowired
    private ISessionService sessionService;

    @GetMapping("/me")
    public ResponseEntity<?> getAktuellenBenutzer(@AuthenticationPrincipal Jwt jwt) {
        // Überprüfen, ob eine gültige Session existiert
        if (!sessionService.isLoggedIn(jwt)) {
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Nicht angemeldet.");
        }


        // Benutzer aus der Datenbank laden
        return benutzerRepository.findByOauthId(jwt.getSubject())
                .map(benutzer -> ResponseEntity.ok(benutzer))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}