package com.ordermymeal.auth.repository;

import com.ordermymeal.auth.model.AuthPurpose;
import com.ordermymeal.auth.model.OneTimeCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OneTimeCodeRepository
        extends JpaRepository<OneTimeCode, UUID> {

    Optional<OneTimeCode>
    findTopByEmailAndPurposeAndConsumedAtIsNullAndExpiresAtAfterOrderByCreatedAtDesc(
            String email,
            AuthPurpose purpose,
            Instant now
    );

    long countByEmailAndCreatedAtAfter(
            String email,
            Instant since
    );

    long countByRequestedIpAndCreatedAtAfter(
            String requestedIp,
            Instant since
    );
}
