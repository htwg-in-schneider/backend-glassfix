package de.htwg.in.schneider.glassfix.backend.service;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import de.htwg.in.schneider.glassfix.backend.model.Benutzer;
import de.htwg.in.schneider.glassfix.backend.model.Rolle;
import org.springframework.security.oauth2.jwt.Jwt;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.htwg.in.schneider.glassfix.backend.repository.BenutzerRepository;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class SessionService implements ISessionService {

    @Autowired
    private BenutzerRepository benutzerRepository;

    private static final Logger LOG = LoggerFactory.getLogger(SessionService.class);

    @Override
    public boolean isLoggedIn(Jwt jwt) {
        return jwt != null && jwt.getSubject() != null;
    }

    @Override
    public boolean hasRole(Jwt jwt, Rolle rolle) {
        if (jwt == null || jwt.getSubject() == null) {
            LOG.warn("JWT or subject is null");
            return false;
        }
        Optional<Benutzer> user = benutzerRepository.findByOauthId(jwt.getSubject());
        if (!user.isPresent()) {
            LOG.warn("No user found for JWT subject: " + jwt.getSubject());
            return false;
        }
        boolean hasRole = user.get().getRolle() == rolle;
        LOG.info("User with OauthId " + jwt.getSubject() + " has role " + user.get().getRolle() + ". Required role: " + rolle + ". Has required role: " + hasRole);
        return hasRole;
    }

    @Override
    public String getOauthId(Jwt jwt) {
        if (jwt == null || jwt.getSubject() == null) {
            LOG.warn("JWT or subject is null");
            return null;
        }
        
        return jwt.getSubject();
    }

    @Override
    public Long getUserId(Jwt jwt) {
        if (jwt == null || jwt.getSubject() == null) {
            LOG.warn("JWT or subject is null");
            return null;
        }
        Optional<Benutzer> user = benutzerRepository.findByOauthId(jwt.getSubject());
        if (!user.isPresent()) {
            LOG.warn("No user found for JWT subject: " + jwt.getSubject());
            return null;
        }
        return user.get().getId();
    }
}