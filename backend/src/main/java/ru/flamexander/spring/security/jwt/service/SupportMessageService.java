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
        return supportMessageRepository.save(message);
    }

    public List<SupportMessage> getAllSupportMessages() {
        return supportMessageRepository.findAll();
    }
}