package com.ordermymeal.auth.service;

import com.ordermymeal.auth.model.AuthSession;
import com.ordermymeal.auth.model.User;
import com.ordermymeal.auth.repository.SessionRepository;
import com.ordermymeal.auth.repository.UserRepository;
import com.ordermymeal.membership.model.OrganizationMember;
import com.ordermymeal.membership.repository.OrganizationMemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class SetPasswordService {

    private final SessionRepository sessionRepository;
    private final SessionTokenService sessionTokenService;
    private final OrganizationMemberRepository membershipRepository;
    private final UserRepository userRepository;
    private final PasswordEncoderService passwordEncoderService;

    public SetPasswordService(
            SessionRepository sessionRepository,
            SessionTokenService sessionTokenService,
            OrganizationMemberRepository membershipRepository,
            UserRepository userRepository,
            PasswordEncoderService passwordEncoderService) {
        this.sessionRepository = sessionRepository;
        this.sessionTokenService = sessionTokenService;
        this.membershipRepository = membershipRepository;
        this.userRepository = userRepository;
        this.passwordEncoderService = passwordEncoderService;
    }

    @Transactional
    public void setPassword(String sessionToken, String rawPassword) {
        if (sessionToken == null || sessionToken.isBlank()) {
            throw new IllegalArgumentException("Authentication is required.");
        }

        Instant now = Instant.now();

        AuthSession session = sessionRepository
                .findByTokenHashAndRevokedAtIsNullAndExpiresAtAfter(
                        sessionTokenService.hashToken(sessionToken), now)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Session is invalid or expired."));

        OrganizationMember membership = membershipRepository
                .findById(session.getMembershipId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Membership not found."));

        if (!"ACTIVE".equalsIgnoreCase(membership.getStatus())) {
            throw new IllegalArgumentException("Membership is not active.");
        }

        User user = userRepository.findById(membership.getUser().getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        user.setPasswordHash(passwordEncoderService.encode(rawPassword));
        userRepository.save(user);

    }
}
