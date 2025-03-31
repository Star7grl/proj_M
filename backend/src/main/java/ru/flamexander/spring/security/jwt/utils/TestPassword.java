package ru.flamexander.spring.security.jwt.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestPassword {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String plainPassword = "Admin123!";
        String hashedPassword = encoder.encode(plainPassword);
        System.out.println("Захешированный пароль: " + hashedPassword);
    }
}