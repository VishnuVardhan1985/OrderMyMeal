package com.ordermymeal.auth.controller;

import com.ordermymeal.auth.dto.*;
import com.ordermymeal.auth.service.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private static final String SESSION_COOKIE = "OMM_SESSION";

    private final RequestOtpService requestOtpService;
    private final VerifyOtpService verifyOtpService;
    private final PasswordLoginService passwordLoginService;
    private final SetPasswordService setPasswordService;
    private final LogoutService logoutService;

    public AuthController(
            RequestOtpService requestOtpService,
            VerifyOtpService verifyOtpService,
            PasswordLoginService passwordLoginService,
            SetPasswordService setPasswordService,
            LogoutService logoutService) {
        this.requestOtpService = requestOtpService;
        this.verifyOtpService = verifyOtpService;
        this.passwordLoginService = passwordLoginService;
        this.setPasswordService = setPasswordService;
        this.logoutService = logoutService;
    }

    @PostMapping("/otp")
    public ResponseEntity<RequestOtpResponse> requestOtp(
            @Valid @RequestBody RequestOtpRequest request,
            HttpServletRequest httpRequest) {

        RequestOtpResponse response = requestOtpService.requestOtp(
                request.email(), httpRequest.getRemoteAddr());

        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .body(response);
    }

    @PostMapping("/otp/verify")
    public ResponseEntity<LoginResponse> verifyOtp(
            @Valid @RequestBody VerifyOtpRequest request,
            HttpServletResponse httpResponse) {

        AuthenticationSessionService.SessionResult result =
                verifyOtpService.verifyOtp(
                        request.email(),
                        request.otp(),
                        request.organizationId());

        addSessionCookie(httpResponse, result.token());

        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .body(result.response());
    }

    @PostMapping("/password")
    public ResponseEntity<LoginResponse> passwordLogin(
            @Valid @RequestBody PasswordLoginRequest request,
            HttpServletResponse httpResponse) {

        AuthenticationSessionService.SessionResult result =
                passwordLoginService.login(
                        request.email(),
                        request.password(),
                        request.organizationId());

        addSessionCookie(httpResponse, result.token());

        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .body(result.response());
    }

    @PostMapping("/password/set")
    public ResponseEntity<Void> setPassword(
            @Valid @RequestBody SetPasswordRequest request,
            HttpServletRequest httpRequest) {

        setPasswordService.setPassword(
                extractSessionToken(httpRequest),
                request.password());

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logout(
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {

        LogoutResponse response =
                logoutService.logout(extractSessionToken(httpRequest));

        clearSessionCookie(httpResponse);

        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .body(response);
    }

    private void addSessionCookie(
            HttpServletResponse response,
            String token) {

        Cookie cookie = new Cookie(SESSION_COOKIE, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // true when served over HTTPS
        cookie.setPath("/");
        cookie.setMaxAge((int) Duration.ofDays(30).toSeconds());
        response.addCookie(cookie);
    }

    private void clearSessionCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(SESSION_COOKIE, "");
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    private String extractSessionToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (SESSION_COOKIE.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }
}
