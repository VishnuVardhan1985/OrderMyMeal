package com.ordermymeal.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Service
public class OtpHashService {

    private final byte[] secret;

    public OtpHashService(
            @Value("${ordermymeal.auth.otp-hash-secret}") String secret
    ) {
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
    }

    public byte[] hash(String email, String otp) {

        try {
            Mac mac = Mac.getInstance("HmacSHA256");

            SecretKeySpec key = new SecretKeySpec(
                    secret,
                    "HmacSHA256"
            );

            mac.init(key);

            String value = email.toLowerCase().trim() + ":" + otp;

            return mac.doFinal(
                    value.getBytes(StandardCharsets.UTF_8)
            );

        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Unable to hash OTP",
                    exception
            );
        }
    }
}
