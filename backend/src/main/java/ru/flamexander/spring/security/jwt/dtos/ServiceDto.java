package ru.flamexander.spring.security.jwt.dtos;

import lombok.*;

import javax.persistence.*;


@Getter
@Setter
@NoArgsConstructor // Пустой конструктор
@AllArgsConstructor // Конструктор со всеми параметрамин
public class ServiceDto {
    private Long serviceId;
    private String serviceName;
    private Double servicePrice;// Можно использовать Enum для статусов
}