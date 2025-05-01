package ru.flamexander.spring.security.jwt.dtos;

import lombok.Data;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class BookingDto {
    private Long bookingId;

    @NotNull(message = "User ID cannot be null")
    private Long userId;

    private Long roomId; // Сделано необязательным

    @NotNull(message = "Check-in date cannot be null")
    private LocalDate checkInDate;

    @NotNull(message = "Check-out date cannot be null")
    private LocalDate checkOutDate;

    private List<Long> serviceIds; // Список ID услуг
    private BigDecimal totalSum;
    private String status;
}