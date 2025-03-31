package ru.flamexander.spring.security.jwt.service;

import org.springframework.stereotype.Service;

@Service
public class AdminPanelService {
    // Новое: простой метод для возврата данных админ-панели
    public String getAdminData() {
        return "Данные для админ-панели";
    }
}