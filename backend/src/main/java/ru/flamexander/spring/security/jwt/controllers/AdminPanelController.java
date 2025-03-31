package ru.flamexander.spring.security.jwt.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.flamexander.spring.security.jwt.service.AdminPanelService;

@RestController
@RequestMapping("/api/admin")
public class AdminPanelController {
    private final AdminPanelService adminPanelService;

    @Autowired
    public AdminPanelController(AdminPanelService adminPanelService) {
        this.adminPanelService = adminPanelService;
    }

    // Новое: эндпоинт для получения данных админ-панели
    @GetMapping
    public ResponseEntity<?> getAdminPanelData() {
        return ResponseEntity.ok(adminPanelService.getAdminData());
    }
}