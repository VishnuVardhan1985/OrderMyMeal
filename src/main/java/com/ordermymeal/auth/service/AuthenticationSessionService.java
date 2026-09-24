package com.ordermymeal.auth.service;

import com.ordermymeal.auth.config.AuthProperties;
import com.ordermymeal.auth.dto.LoginResponse;
import com.ordermymeal.auth.model.AuthSession;
import com.ordermymeal.auth.model.Role;
import com.ordermymeal.auth.repository.SessionRepository;
import com.ordermymeal.membership.model.MembershipRole;
import com.ordermymeal.membership.model.OrganizationMember;
import com.ordermymeal.membership.repository.MembershipRoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class AuthenticationSessionService {

        private final SessionRepository sessionRepository;
        private final SessionTokenService sessionTokenService;
        private final MembershipRoleRepository membershipRoleRepository;
        private final AuthProperties authProperties;

        public AuthenticationSessionService(
                        SessionRepository sessionRepository,
                        SessionTokenService sessionTokenService,
                        MembershipRoleRepository membershipRoleRepository,
                        AuthProperties authProperties) {
                this.sessionRepository = sessionRepository;
                this.sessionTokenService = sessionTokenService;
                this.membershipRoleRepository = membershipRoleRepository;
                this.authProperties = authProperties;
        }

        @Transactional
        public SessionResult createSession(OrganizationMember membership) {
                Instant now = Instant.now();
                String token = sessionTokenService.generateToken();

                AuthSession session = new AuthSession(
                                UUID.randomUUID(),
                                membership.getMembershipId(),
                                sessionTokenService.hashToken(token),
                                now,
                                now.plus(authProperties.getSessionLifetime()),
                                null,
                                now,
                                now);

                sessionRepository.save(session);

                List<Role> roles = membershipRoleRepository
                                .findByMembershipId(membership.getMembershipId())
                                .stream()
                                .map(MembershipRole::getRole)
                                .toList();

                Long organizationId = membership.getOrganization() == null
                                ? null
                                : membership.getOrganization().getOrgId();

                return new SessionResult(
                                token,
                                new LoginResponse(
                                                "Login successful.",
                                                membership.getMembershipId(),
                                                organizationId,
                                                roles));
        }

        public record SessionResult(String token, LoginResponse response) {
        }
}
