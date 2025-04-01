package ru.flamexander.spring.security.jwt.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.flamexander.spring.security.jwt.service.UserService; // Правильный импорт

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class PasswordResetController {

    @Autowired
    private UserService userService;

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        boolean sent = userService.sendResetCode(email);
        if (sent) {
            return ResponseEntity.ok("Код отправлен на вашу почту");
        } else {
            return ResponseEntity.badRequest().body("Почта не найдена");
        }
    }

    @PostMapping("/verify-code")
    public ResponseEntity<String> verifyCode(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String code = body.get("code");
        boolean verified = userService.verifyResetCode(email, code);
        if (verified) {
            return ResponseEntity.ok("Код верен");
        } else {
            return ResponseEntity.badRequest().body("Неверный код");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String newPassword = body.get("newPassword");
        userService.resetPassword(email, newPassword);
        return ResponseEntity.ok("Пароль обновлен");
    }
}