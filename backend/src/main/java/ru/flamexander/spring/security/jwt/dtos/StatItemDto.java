package ru.flamexander.spring.security.jwt.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatItemDto {
    private String name;
    private Long value; // Количество бронирований/использований
    // Цвет можно назначать на фронтенде для гибкости
}
