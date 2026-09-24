package com.ordermymeal.auth.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "one_time_codes")
public class OneTimeCode {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String email;

    @Column(name = "user_id")
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthPurpose purpose;

    @Column(name = "code_hash", nullable = false, columnDefinition = "bytea")
    private byte[] codeHash;

    @Column(nullable = false)
    private int attempts;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "consumed_at")
    private Instant consumedAt;

    @Column(name = "requested_ip")
    private String requestedIp;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected OneTimeCode() {
    }

    public OneTimeCode(
            UUID id,
            String email,
            Long userId,
            AuthPurpose purpose,
            byte[] codeHash,
            int attempts,
            Instant expiresAt,
            Instant consumedAt,
            String requestedIp,
            Instant createdAt,
            Instant updatedAt) {

        this.id = id;
        this.email = email;
        this.userId = userId;
        this.purpose = purpose;
        this.codeHash = codeHash;
        this.attempts = attempts;
        this.expiresAt = expiresAt;
        this.consumedAt = consumedAt;
        this.requestedIp = requestedIp;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public Long getUserId() {
        return userId;
    }

    public AuthPurpose getPurpose() {
        return purpose;
    }

    public byte[] getCodeHash() {
        return codeHash;
    }

    public int getAttempts() {
        return attempts;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public Instant getConsumedAt() {
        return consumedAt;
    }

    public String getRequestedIp() {
        return requestedIp;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void incrementAttempts() {
        this.attempts++;
        this.updatedAt = Instant.now();
    }

    public void consume() {
        this.consumedAt = Instant.now();
        this.updatedAt = Instant.now();
    }
}