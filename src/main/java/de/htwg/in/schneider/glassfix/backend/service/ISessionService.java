package de.htwg.in.schneider.glassfix.backend.service;

import jakarta.servlet.http.HttpSession;
import de.htwg.in.schneider.glassfix.backend.model.Benutzer;
import de.htwg.in.schneider.glassfix.backend.model.Rolle;

public interface ISessionService {
    boolean isLoggedIn(HttpSession session);
    boolean hasRole(HttpSession session, Rolle rolle);
    String getOauthId(HttpSession session);
    Long getUserId(HttpSession session);
    void addSession(HttpSession session, Benutzer benutzer);
    void removeSession(HttpSession session);
}