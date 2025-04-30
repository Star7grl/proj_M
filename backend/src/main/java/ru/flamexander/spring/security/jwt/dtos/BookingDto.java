package ru.flamexander.spring.security.jwt.dtos;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class BookingDto {
    private Long bookingId;

    @NotNull(message = "User ID cannot be null")
    private Long userId;

    @NotNull(message = "Room ID cannot be null")
    private Long roomId;

    @NotNull(message = "Check-in date cannot be null")
    private LocalDate checkInDate;

    @NotNull(message = "Check-out date cannot be null")
    private LocalDate checkOutDate;

    private Long serviceId;
    private BigDecimal totalSum;
    private String status; // Новое поле для статуса
}