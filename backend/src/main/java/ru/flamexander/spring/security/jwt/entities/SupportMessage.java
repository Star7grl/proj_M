package ru.flamexander.spring.security.jwt.entities;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "support_messages")
public class SupportMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "message_text", nullable = false)
    private String messageText;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "status", nullable = false)
    private String status = "Не назначен"; // Статус по умолчанию

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}