package com.ordermymeal.auth.repository;

import com.ordermymeal.auth.model.AuthSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SessionRepository
        extends JpaRepository<AuthSession, UUID> {

    Optional<AuthSession> findByTokenHashAndRevokedAtIsNullAndExpiresAtAfter(
            byte[] tokenHash,
            Instant now
    );

    Optional<AuthSession> findByTokenHash(
            byte[] tokenHash
    );
}
