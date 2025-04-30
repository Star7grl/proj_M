package ru.flamexander.spring.security.jwt.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "room_images")
@Getter
@Setter
public class RoomImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "room_id")
    @JsonIgnore // Игнорируем поле room при сериализации JSON, чтобы избежать циклической зависимости
    private Room room;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    // Мой комментарий: Сущность RoomImage связана с Room через ManyToOne, поле imageUrl использует TEXT для длинных URL.
    // Добавлена аннотация @JsonIgnore, чтобы предотвратить бесконечную рекурсию при сериализации JSON.
}