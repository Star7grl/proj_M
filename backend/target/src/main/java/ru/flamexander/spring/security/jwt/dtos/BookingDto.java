package ru.flamexander.spring.security.jwt.dtos;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class BookingDto {
    private Long bookingId;
    private Long userId;
    private Long roomId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Long serviceId;
    private BigDecimal totalSum;
}


