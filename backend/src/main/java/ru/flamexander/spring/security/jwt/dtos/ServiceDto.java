package ru.flamexander.spring.security.jwt.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceDto {
    private Long serviceId;
    private String serviceName;
    private Double servicePrice;
    private String imageUrl;
    private String description; // Добавлено поле description
}