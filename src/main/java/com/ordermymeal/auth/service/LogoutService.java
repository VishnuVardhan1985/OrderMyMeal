package com.ordermymeal.auth.service;

import com.ordermymeal.auth.dto.LogoutResponse;
import com.ordermymeal.auth.model.AuthSession;
import com.ordermymeal.auth.repository.SessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class LogoutService {

    private final SessionRepository sessionRepository;
    private final SessionTokenService sessionTokenService;

    public LogoutService(
            SessionRepository sessionRepository,
            SessionTokenService sessionTokenService
    ) {
        this.sessionRepository = sessionRepository;
        this.sessionTokenService = sessionTokenService;
    }

    @Transactional
    public LogoutResponse logout(String sessionToken) {

        if (sessionToken == null || sessionToken.isBlank()) {

            return new LogoutResponse(
                    "Logout successful."
            );
        }

        byte[] tokenHash =
                sessionTokenService.hashToken(
                        sessionToken
                );

        AuthSession session =
                sessionRepository
                        .findByTokenHash(tokenHash)
                        .orElse(null);

        if (session != null &&
                session.getRevokedAt() == null &&
                session.getExpiresAt().isAfter(Instant.now())) {

            session.revoke();

            sessionRepository.save(session);
        }

        return new LogoutResponse(
                "Logout successful."
        );
    }
}
