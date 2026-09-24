package com.ordermymeal.notification.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationService {

    private final JavaMailSender mailSender;

    public EmailNotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOtp(String email, String otp) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(email);
        message.setSubject("OrderMyMeal Login OTP");

        message.setText(
                "Your OrderMyMeal login OTP is: "
                        + otp
                        + "\n\n"
                        + "This OTP expires in 5 minutes."
                        + "\n\n"
                        + "If you did not request this OTP, please ignore this email.");

        mailSender.send(message);
    }
}