package ru.flamexander.spring.security.jwt.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class MainController {
    @GetMapping("/unsecured")
    public String unsecuredData() {
        return "Имеет доступ в Unsecured страница";
    }

    @GetMapping("/secured")
    public String securedData() {
        return "Имеет доступ в Secured страница";
    }

    @GetMapping("/admin")
    public String adminData() {
        return "Имеет доступ в Admin страница";
    }

    @GetMapping("/info")
    public String userData(Principal principal) {
        return principal.getName();
    }
}