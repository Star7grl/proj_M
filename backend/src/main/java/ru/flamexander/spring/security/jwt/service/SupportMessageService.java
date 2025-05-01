package ru.flamexander.spring.security.jwt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.flamexander.spring.security.jwt.entities.SupportMessage;
import ru.flamexander.spring.security.jwt.repositories.SupportMessageRepository;

import java.util.List;

@Service
public class SupportMessageService {
    private final SupportMessageRepository supportMessageRepository;

    @Autowired
    public SupportMessageService(SupportMessageRepository supportMessageRepository) {
        this.supportMessageRepository = supportMessageRepository;
    }

    public SupportMessage createSupportMessage(SupportMessage message) {
        message.setStatus("Не назначен"); // Устанавливаем статус по умолчанию
        return supportMessageRepository.save(message);
    }

    public List<SupportMessage> getAllSupportMessages() {
        return supportMessageRepository.findAll();
    }

    public SupportMessage updateMessageStatus(Long id, String status) {
        SupportMessage message = supportMessageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Сообщение не найдено"));
        if (!isValidStatus(status)) {
            throw new IllegalArgumentException("Недопустимый статус: " + status);
        }
        message.setStatus(status);
        return supportMessageRepository.save(message);
    }

    private boolean isValidStatus(String status) {
        return "Не назначен".equals(status) || "На рассмотрении".equals(status) || "Решён".equals(status);
    }
}