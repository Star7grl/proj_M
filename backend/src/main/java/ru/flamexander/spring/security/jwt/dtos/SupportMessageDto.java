package ru.flamexander.spring.security.jwt.dtos;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SupportMessageDto {
    private Long id;
    private String messageText;
    private String username;
    private LocalDateTime createdAt;
    private String status;

    public SupportMessageDto() {}

    public SupportMessageDto(Long id, String messageText, String username, LocalDateTime createdAt, String status) {
        this.id = id;
        this.messageText = messageText;
        this.username = username;
        this.createdAt = createdAt;
        this.status = status;
    }
}