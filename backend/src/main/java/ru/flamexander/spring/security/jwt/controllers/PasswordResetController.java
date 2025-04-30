package ru.flamexander.spring.security.jwt.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.flamexander.spring.security.jwt.service.UserService;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class PasswordResetController {

    @Autowired
    private UserService userService;

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        boolean sent = userService.sendResetToken(email);
        if (sent) {
            return ResponseEntity.ok("Ссылка для сброса пароля отправлена на вашу почту");
        } else {
            return ResponseEntity.badRequest().body("Почта не найдена");
        }
    }

    @GetMapping("/reset-password")
    public ResponseEntity<String> validateToken(@RequestParam String token) {
        boolean isValid = userService.validateResetToken(token);
        if (isValid) {
            return ResponseEntity.ok("Токен валиден");
        } else {
            return ResponseEntity.badRequest().body("Недействительный или просроченный токен");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        String newPassword = body.get("newPassword");
        try {
            userService.resetPassword(token, newPassword);
            return ResponseEntity.ok("Пароль успешно изменен");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}