package com.ordermymeal.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.time.Instant;

@Service
public class SessionTokenService {

    private final SecureRandom secureRandom = new SecureRandom();

    private final byte[] secret;

    public SessionTokenService(
            @Value("${ordermymeal.auth.session-hash-secret}") String secret
    ) {
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
    }

    public String generateToken() {

        byte[] token = new byte[32];

        secureRandom.nextBytes(token);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(token);
    }

    public byte[] hashToken(String token) {

        try {
            Mac mac = Mac.getInstance("HmacSHA256");

            SecretKeySpec key = new SecretKeySpec(
                    secret,
                    "HmacSHA256"
            );

            mac.init(key);

            return mac.doFinal(
                    token.getBytes(StandardCharsets.UTF_8)
            );

        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Unable to hash session token",
                    exception
            );
        }
    }
}
