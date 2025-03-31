package ru.flamexander.spring.security.jwt.dtos;

import lombok.*;

import javax.persistence.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomDto {
    private Integer roomId;
    private String roomTitle;
    private String roomType;
    private String description;
    private Double price;
    private String status; // Можно использовать Enum
}