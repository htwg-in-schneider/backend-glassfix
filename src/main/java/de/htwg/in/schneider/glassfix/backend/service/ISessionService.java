package de.htwg.in.schneider.glassfix.backend.service;

import org.springframework.security.oauth2.jwt.Jwt;

import jakarta.servlet.http.HttpSession;
import de.htwg.in.schneider.glassfix.backend.model.Benutzer;
import de.htwg.in.schneider.glassfix.backend.model.Rolle;

public interface ISessionService {
    boolean isLoggedIn(Jwt jwt);
    boolean hasRole(Jwt jwt, Rolle rolle);
    String getOauthId(Jwt jwt);
    Long getUserId(Jwt jwt);
}