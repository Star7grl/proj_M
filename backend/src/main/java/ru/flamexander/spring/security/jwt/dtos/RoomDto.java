package ru.flamexander.spring.security.jwt.dtos;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomDto {
    private Long roomId;
    private String roomTitle;
    private String roomType;
    private String description;
    private Double price;
    private String status;
    // private String imageUrl; // Новое поле для URL изображения
    // Мой комментарий: Поле imageUrl заменено на список imageUrls для поддержки нескольких изображений.
    private List<String> imageUrls;
}