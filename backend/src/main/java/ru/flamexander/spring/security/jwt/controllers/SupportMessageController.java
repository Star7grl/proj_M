package ru.flamexander.spring.security.jwt.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.flamexander.spring.security.jwt.dtos.SupportMessageDto;
import ru.flamexander.spring.security.jwt.entities.SupportMessage;
import ru.flamexander.spring.security.jwt.entities.User;
import ru.flamexander.spring.security.jwt.service.SupportMessageService;
import ru.flamexander.spring.security.jwt.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/support")
public class SupportMessageController {
    private final SupportMessageService supportMessageService;
    private final UserService userService;

    @Autowired
    public SupportMessageController(SupportMessageService supportMessageService, UserService userService) {
        this.supportMessageService = supportMessageService;
        this.userService = userService;
    }

    @PostMapping("/send")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> sendSupportMessage(@RequestBody SupportMessageDto messageDto) {
        User user = userService.getCurrentUser(); // Предполагается, что метод существует в UserService
        SupportMessage message = new SupportMessage();
        message.setMessageText(messageDto.getMessageText());
        message.setUser(user);
        supportMessageService.createSupportMessage(message);
        return ResponseEntity.ok("Сообщение успешно отправлено");
    }

    @GetMapping("/messages")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SupportMessageDto>> getAllSupportMessages() {
        List<SupportMessage> messages = supportMessageService.getAllSupportMessages();
        List<SupportMessageDto> messageDtos = messages.stream()
                .map(message -> new SupportMessageDto(message.getId(), message.getMessageText(), message.getUser().getUsername(), message.getCreatedAt()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(messageDtos);
    }
}