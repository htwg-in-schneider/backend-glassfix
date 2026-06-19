package de.htwg.in.schneider.glassfix.backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import de.htwg.in.schneider.glassfix.backend.model.Benutzer;
import de.htwg.in.schneider.glassfix.backend.repository.BenutzerRepository;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    static final Logger LOGGER = LoggerFactory.getLogger(ProfileController.class);
    
    @Autowired
    private BenutzerRepository benutzerRepository;

    @GetMapping
    public ResponseEntity<Benutzer> getProfile(@AuthenticationPrincipal Jwt jwt) {
        String oauthId = jwt.getSubject();
        LOGGER.info("getProfile called for principal: {}", oauthId);
        LOGGER.debug("JWT claims: {}", jwt.getClaims());
        
        if (oauthId == null) {
            LOGGER.warn("JWT does not contain 'sub' claim");
            return ResponseEntity.badRequest().build();
        }
        return benutzerRepository.findByOauthId(oauthId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}