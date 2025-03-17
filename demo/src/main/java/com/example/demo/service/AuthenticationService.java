package com.example.demo.service;

import com.example.demo.dto.LoginUserDto;
import com.example.demo.dto.RegisterUserDto;
import com.example.demo.dto.VerifyUserDto;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import jakarta.mail.MessagingException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            EmailService emailService
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public User signup(RegisterUserDto input) {
        User user = new User(input.getUsername(), input.getEmail(), passwordEncoder.encode(input.getPassword()));
        user.setVerificationCode(generateVerificationCode());
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
        user.setEnabled(false);
        sendVerificationEmail(user);
        return userRepository.save(user);
    }

    public User authenticate(LoginUserDto input) {
        User user = userRepository.findByEmail(input.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isEnabled()) {
            throw new RuntimeException("Account not verified. Please verify your account.");
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );

        return user;
    }

    public void verifyUser(VerifyUserDto input) {
        Optional<User> optionalUser = userRepository.findByEmail(input.getEmail());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("Verification code has expired");
            }
            if (user.getVerificationCode().equals(input.getVerificationCode())) {
                user.setEnabled(true);
                user.setVerificationCode(null);
                user.setVerificationCodeExpiresAt(null);
                userRepository.save(user);
            } else {
                throw new RuntimeException("Invalid verification code");
            }
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public void resendVerificationCode(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.isEnabled()) {
                throw new RuntimeException("Account is already verified");
            }
            user.setVerificationCode(generateVerificationCode());
            user.setVerificationCodeExpiresAt(LocalDateTime.now().plusHours(1));
            sendVerificationEmail(user);
            userRepository.save(user);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    private void sendVerificationEmail(User user) {
        String subject = "Account Verification";
        String verificationCode = user.getVerificationCode();

        String htmlMessage = "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<title>Verification Code</title>" +
                "<style>" +
                "body { font-family: 'Arial', sans-serif; background-color: #000; color: #fff; padding: 20px; margin: 0; text-align: center; }" +
                ".container { max-width: 600px; margin: auto; background: linear-gradient(135deg, #1a0033, #3a0099, #1a0033); padding: 30px; border-radius: 15px; " +
                "box-shadow: 0 0 40px rgba(0, 255, 255, 1); border: 3px solid rgba(0, 255, 255, 1); }" +
                ".header { font-size: 32px; font-weight: bold; text-transform: uppercase; color: #0ff; " +
                "text-shadow: 0 0 20px #00ffff, 0 0 40px #00ffff, 0 0 60px #00ffff; margin-bottom: 10px; letter-spacing: 2px; }" +
                ".content { padding: 20px; font-size: 18px; color: #eee; text-shadow: 0 0 10px rgba(255, 255, 255, 1); }" +
                ".code-box { background: rgba(0, 255, 255, 0.7); padding: 20px; display: inline-block; font-size: 40px; font-weight: bold; " +
                "color: #000; border-radius: 8px; letter-spacing: 8px; text-shadow: 0 0 25px #00ffff, 0 0 50px #00ffff, 0 0 75px #00ffff; " +
                "border: 3px solid #00ffff; animation: pulse 1.5s infinite alternate; }" +
                "@keyframes pulse { from { text-shadow: 0 0 30px #00ffff, 0 0 60px #00ffff; } to { text-shadow: 0 0 50px #00ffff, 0 0 100px #00ffff; } }" +
                ".footer { margin-top: 20px; font-size: 14px; color: #bbb; text-shadow: 0 0 10px rgba(0, 255, 255, 1); }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<div class='header'>&#x1F680; VERIFY YOUR IDENTITY</div>" +
                "<div class='content'>" +
                "<p>You're one step away from securing your account.</p>" +
                "<p>Enter the following code to verify your account:</p>" +
                "<div class='code-box'>" + verificationCode + "</div>" +
                "<p>If you didn’t request this, please ignore this email.</p>" +
                "</div>" +
                "<div class='footer'>© 2024 YourCompany - All Rights Reserved.</div>" +
                "</div>" +
                "</body>" +
                "</html>";


        try {
            emailService.sendVerificationEmail(user.getEmail(), subject, htmlMessage);
        } catch (MessagingException e) {
            // Handle email sending exception
            e.printStackTrace();
        }
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }
}
