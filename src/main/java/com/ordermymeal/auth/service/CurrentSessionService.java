package com.ordermymeal.auth.service;

import com.ordermymeal.auth.model.AuthSession;
import com.ordermymeal.auth.repository.SessionRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Arrays;

@Service
public class CurrentSessionService {

    private static final String SESSION_COOKIE = "OMM_SESSION";

    private final SessionRepository sessionRepository;
    private final SessionTokenService sessionTokenService;

    public CurrentSessionService(
            SessionRepository sessionRepository,
            SessionTokenService sessionTokenService) {
        this.sessionRepository = sessionRepository;
        this.sessionTokenService = sessionTokenService;
    }

    public AuthSession getCurrentSession(HttpServletRequest request) {

        String token = extractSessionToken(request);

        if (token == null || token.isBlank()) {
            throw new AuthorizationException("Authentication required.");
        }

        byte[] tokenHash = sessionTokenService.hashToken(token);

        return sessionRepository
                .findByTokenHashAndRevokedAtIsNullAndExpiresAtAfter(
                        tokenHash,
                        Instant.now())
                .orElseThrow(() -> new AuthorizationException(
                        "Session is invalid or expired."));
    }

    public Long getCurrentMembershipId(HttpServletRequest request) {

        return getCurrentSession(request).getMembershipId();
    }

    private String extractSessionToken(HttpServletRequest request) {

        if (request.getCookies() == null) {
            return null;
        }

        return Arrays.stream(request.getCookies())
                .filter(cookie -> SESSION_COOKIE.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}