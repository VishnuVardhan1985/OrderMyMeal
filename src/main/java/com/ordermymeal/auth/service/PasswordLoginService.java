package com.ordermymeal.auth.service;

import com.ordermymeal.auth.dto.LoginResponse;
import com.ordermymeal.auth.model.User;
import com.ordermymeal.auth.repository.UserRepository;
import com.ordermymeal.membership.model.OrganizationMember;
import com.ordermymeal.membership.repository.OrganizationMemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PasswordLoginService {

    private final UserRepository userRepository;
    private final OrganizationMemberRepository membershipRepository;
    private final PasswordEncoderService passwordEncoderService;
    private final AuthenticationSessionService sessionService;

    public PasswordLoginService(
            UserRepository userRepository,
            OrganizationMemberRepository membershipRepository,
            PasswordEncoderService passwordEncoderService,
            AuthenticationSessionService sessionService) {
        this.userRepository = userRepository;
        this.membershipRepository = membershipRepository;
        this.passwordEncoderService = passwordEncoderService;
        this.sessionService = sessionService;
    }

    @Transactional
    public AuthenticationSessionService.SessionResult login(
            String email,
            String password,
            Long organizationId) {

        String normalizedEmail = email.trim().toLowerCase();

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));

        if (!"ACTIVE".equalsIgnoreCase(user.getStatus())
                || !passwordEncoderService.matches(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password.");
        }

        List<OrganizationMember> memberships =
                membershipRepository.findByUserUserIdAndStatusOrderByMembershipIdAsc(
                        user.getUserId(), "ACTIVE");

        OrganizationMember membership = selectMembership(memberships, organizationId);

        return sessionService.createSession(membership);
    }

    private OrganizationMember selectMembership(
            List<OrganizationMember> memberships,
            Long organizationId) {

        if (memberships.isEmpty()) {
            throw new IllegalArgumentException("No active organization membership found.");
        }

        if (organizationId != null) {
            return memberships.stream()
                    .filter(m -> m.getOrganization() != null
                            && organizationId.equals(m.getOrganization().getOrgId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(
                            "No active membership found for the selected organization."));
        }

        if (memberships.size() > 1) {
            throw new IllegalArgumentException(
                    "Multiple active memberships found. organizationId is required.");
        }

        return memberships.get(0);
    }
}
