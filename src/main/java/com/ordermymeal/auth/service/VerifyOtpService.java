package com.ordermymeal.auth.service;

import com.ordermymeal.auth.config.AuthProperties;
import com.ordermymeal.auth.model.AuthPurpose;
import com.ordermymeal.auth.model.OneTimeCode;
import com.ordermymeal.auth.model.User;
import com.ordermymeal.auth.repository.OneTimeCodeRepository;
import com.ordermymeal.auth.repository.UserRepository;
import com.ordermymeal.membership.model.OrganizationMember;
import com.ordermymeal.membership.repository.OrganizationMemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class VerifyOtpService {

    private final OneTimeCodeRepository oneTimeCodeRepository;
    private final UserRepository userRepository;
    private final OrganizationMemberRepository organizationMemberRepository;
    private final com.ordermymeal.auth.service.OtpHashService otpHashService;
    private final AuthenticationSessionService sessionService;
    private final AuthProperties authProperties;

    public VerifyOtpService(
            OneTimeCodeRepository oneTimeCodeRepository,
            UserRepository userRepository,
            OrganizationMemberRepository organizationMemberRepository,
            OtpHashService otpHashService,
            AuthenticationSessionService sessionService,
            AuthProperties authProperties) {
        this.oneTimeCodeRepository = oneTimeCodeRepository;
        this.userRepository = userRepository;
        this.organizationMemberRepository = organizationMemberRepository;
        this.otpHashService = otpHashService;
        this.sessionService = sessionService;
        this.authProperties = authProperties;
    }

    @Transactional
    public AuthenticationSessionService.SessionResult verifyOtp(
            String email,
            String otp,
            Long organizationId) {

        String normalizedEmail = email.trim().toLowerCase();
        Instant now = Instant.now();

        OneTimeCode oneTimeCode = oneTimeCodeRepository
                .findTopByEmailAndPurposeAndConsumedAtIsNullAndExpiresAtAfterOrderByCreatedAtDesc(
                        normalizedEmail, AuthPurpose.SIGN_IN, now)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired OTP."));

        if (oneTimeCode.getAttempts() >= authProperties.getMaxOtpAttempts()) {
            throw new IllegalArgumentException("Maximum OTP attempts exceeded.");
        }

        oneTimeCode.incrementAttempts();

        byte[] expectedHash = otpHashService.hash(normalizedEmail, otp);

        if (!constantTimeEquals(expectedHash, oneTimeCode.getCodeHash())) {
            oneTimeCodeRepository.save(oneTimeCode);
            throw new IllegalArgumentException("Invalid or expired OTP.");
        }

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired OTP."));

        if (!"ACTIVE".equalsIgnoreCase(user.getStatus())) {
            throw new IllegalArgumentException("Account is not active.");
        }

        List<OrganizationMember> memberships =
                organizationMemberRepository.findByUserUserIdAndStatusOrderByMembershipIdAsc(
                        user.getUserId(), "ACTIVE");

        OrganizationMember membership = selectMembership(memberships, organizationId);

        oneTimeCode.consume();
        oneTimeCodeRepository.save(oneTimeCode);

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

    private boolean constantTimeEquals(byte[] first, byte[] second) {
        if (first == null || second == null || first.length != second.length) {
            return false;
        }

        int result = 0;
        for (int i = 0; i < first.length; i++) {
            result |= first[i] ^ second[i];
        }
        return result == 0;
    }
}
