package com.ordermymeal.auth.service;

import com.ordermymeal.auth.config.AuthProperties;
import com.ordermymeal.auth.dto.RequestOtpResponse;
import com.ordermymeal.auth.model.AuthPurpose;
import com.ordermymeal.auth.model.OneTimeCode;
import com.ordermymeal.auth.repository.OneTimeCodeRepository;
import com.ordermymeal.notification.service.EmailNotificationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class RequestOtpService {

        private static final Logger log = LoggerFactory.getLogger(RequestOtpService.class);

        private final OneTimeCodeRepository oneTimeCodeRepository;
        private final OtpGenerator otpGenerator;
        private final OtpHashService otpHashService;
        private final AuthProperties authProperties;
        private final EmailNotificationService emailNotificationService;

        public RequestOtpService(
                        OneTimeCodeRepository oneTimeCodeRepository,
                        OtpGenerator otpGenerator,
                        OtpHashService otpHashService,
                        AuthProperties authProperties,
                        EmailNotificationService emailNotificationService) {
                this.oneTimeCodeRepository = oneTimeCodeRepository;
                this.otpGenerator = otpGenerator;
                this.otpHashService = otpHashService;
                this.authProperties = authProperties;
                this.emailNotificationService = emailNotificationService;
        }

        @Transactional
        public RequestOtpResponse requestOtp(
                        String email,
                        String requestedIp) {

                String normalizedEmail = normalizeEmail(email);

                Instant now = Instant.now();

                Instant emailWindow = now.minus(authProperties.getResendWindow());

                long emailRequests = oneTimeCodeRepository.countByEmailAndCreatedAtAfter(
                                normalizedEmail,
                                emailWindow);

                if (emailRequests >= authProperties.getMaxOtpRequestsPerEmail()) {

                        throw new IllegalStateException(
                                        "Too many OTP requests. Please try again later.");
                }

                long ipRequests = oneTimeCodeRepository.countByRequestedIpAndCreatedAtAfter(
                                requestedIp,
                                emailWindow);

                if (ipRequests >= authProperties.getMaxOtpRequestsPerIp()) {

                        throw new IllegalStateException(
                                        "Too many OTP requests. Please try again later.");
                }

                String otp = otpGenerator.generate();

                byte[] otpHash = otpHashService.hash(
                                normalizedEmail,
                                otp);

                OneTimeCode oneTimeCode = new OneTimeCode(
                                UUID.randomUUID(),
                                normalizedEmail,
                                null,
                                AuthPurpose.SIGN_IN,
                                otpHash,
                                0,
                                now.plus(
                                                authProperties.getOtpExpiry()),
                                null,
                                requestedIp,
                                now,
                                now);

                oneTimeCodeRepository.save(oneTimeCode);

                /*
                 * Send the OTP to the user's real email address.
                 *
                 * The database stores only the OTP hash.
                 * The actual OTP is sent through the notification service.
                 */
                if (authProperties.isDevOtpLogging()) {
                        log.info("DEV ONLY - OTP for {} is {}", normalizedEmail, otp);
                }

                if (!authProperties.isDevOtpOnly()) {
                        emailNotificationService.sendOtp(
                                        normalizedEmail,
                                        otp);
                }

                /*
                 * Deliberately generic response.
                 *
                 * This prevents an attacker from learning
                 * whether an email exists in the system.
                 */
                return new RequestOtpResponse(
                                "If the email is registered, an OTP has been sent.");
        }

        private String normalizeEmail(String email) {
                return email.trim().toLowerCase();
        }
}