package de.htwg.in.schneider.glassfix.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import de.htwg.in.schneider.glassfix.backend.model.Benutzer;
import de.htwg.in.schneider.glassfix.backend.repository.BenutzerRepository;
import de.htwg.in.schneider.glassfix.backend.service.SessionService;
import de.htwg.in.schneider.glassfix.backend.service.ISessionService;
import jakarta.servlet.http.HttpSession;
import de.htwg.in.schneider.glassfix.backend.model.LoginRequest;
import java.util.Optional;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private BenutzerRepository benutzerRepository;

    @Autowired
    private ISessionService sessionService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpSession session) {

        Benutzer benutzer = benutzerRepository.findByEmail(loginRequest.getEmail());

        if (benutzer != null) {
            if (benutzer.getHashpasswort().equals(loginRequest.getPassword())) {
                // Login exitoso
                sessionService.addSession(session, benutzer);
                return ResponseEntity.ok("Successful login for: " + benutzer.getBenutzername());
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        sessionService.removeSession(session);
        return ResponseEntity.ok().build();
    }
}