package de.htwg.in.schneider.glassfix.backend.service;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import de.htwg.in.schneider.glassfix.backend.model.Benutzer;
import de.htwg.in.schneider.glassfix.backend.model.Rolle;

@Service
public class SessionService implements ISessionService {
    
    private static final String USER_ROLE = "UserRole";
    private static final String USER_NAME = "UserName";
    private static final String USER_ID = "UserId";

    @Override
    public boolean isLoggedIn(HttpSession session) {
        return session.getAttribute(USER_ID) != null;
    }

    @Override
    public boolean hasRole(HttpSession session, Rolle rolle) {
        Rolle currentRole = (Rolle) session.getAttribute(USER_ROLE);
        return currentRole != null && currentRole == rolle;
    }

    @Override
    public String getUserName(HttpSession session) {
        return (String) session.getAttribute(USER_NAME);
    }

    @Override
    public Long getUserId(HttpSession session) {
        return (Long) session.getAttribute(USER_ID);
    }

    @Override
    public void addSession(HttpSession session, Benutzer benutzer) {
        session.setAttribute(USER_NAME, benutzer.getBenutzername());
        session.setAttribute(USER_ROLE, benutzer.getRolle());
        session.setAttribute(USER_ID, benutzer.getId());
    }

    @Override
    public void removeSession(HttpSession session) {
        session.invalidate();
    }
}