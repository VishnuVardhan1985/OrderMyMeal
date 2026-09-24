package com.ordermymeal.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.time.Duration;

@Component
@ConfigurationProperties(prefix = "ordermymeal.auth")
public class AuthProperties {

    private Duration otpExpiry = Duration.ofMinutes(5);

    private int maxOtpAttempts = 5;

    private Duration sessionLifetime = Duration.ofDays(30);

    private Duration resendWindow = Duration.ofMinutes(10);

    private int maxOtpRequestsPerEmail = 5;

    private int maxOtpRequestsPerIp = 20;

    private boolean devOtpLogging = true;

    private boolean devOtpOnly = true;

    public Duration getOtpExpiry() {
        return otpExpiry;
    }

    public void setOtpExpiry(Duration otpExpiry) {
        this.otpExpiry = otpExpiry;
    }

    public int getMaxOtpAttempts() {
        return maxOtpAttempts;
    }

    public void setMaxOtpAttempts(int maxOtpAttempts) {
        this.maxOtpAttempts = maxOtpAttempts;
    }

    public Duration getSessionLifetime() {
        return sessionLifetime;
    }

    public void setSessionLifetime(Duration sessionLifetime) {
        this.sessionLifetime = sessionLifetime;
    }

    public Duration getResendWindow() {
        return resendWindow;
    }

    public void setResendWindow(Duration resendWindow) {
        this.resendWindow = resendWindow;
    }

    public int getMaxOtpRequestsPerEmail() {
        return maxOtpRequestsPerEmail;
    }

    public void setMaxOtpRequestsPerEmail(int maxOtpRequestsPerEmail) {
        this.maxOtpRequestsPerEmail = maxOtpRequestsPerEmail;
    }

    public int getMaxOtpRequestsPerIp() {
        return maxOtpRequestsPerIp;
    }

    public void setMaxOtpRequestsPerIp(int maxOtpRequestsPerIp) {
        this.maxOtpRequestsPerIp = maxOtpRequestsPerIp;
    }

    public boolean isDevOtpLogging() {
        return devOtpLogging;
    }

    public void setDevOtpLogging(boolean devOtpLogging) {
        this.devOtpLogging = devOtpLogging;
    }

    public boolean isDevOtpOnly() {
        return devOtpOnly;
    }

    public void setDevOtpOnly(boolean devOtpOnly) {
        this.devOtpOnly = devOtpOnly;
    }
}
