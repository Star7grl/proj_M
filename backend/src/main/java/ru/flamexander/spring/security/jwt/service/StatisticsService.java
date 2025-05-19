package ru.flamexander.spring.security.jwt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.flamexander.spring.security.jwt.dtos.StatItemDto;
import ru.flamexander.spring.security.jwt.entities.Booking;
import ru.flamexander.spring.security.jwt.entities.Rental;
import ru.flamexander.spring.security.jwt.entities.Room;
import ru.flamexander.spring.security.jwt.entities.Services;
import ru.flamexander.spring.security.jwt.repositories.BookingRepository;
import ru.flamexander.spring.security.jwt.repositories.RentalRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final BookingRepository bookingRepository;
    private final RentalRepository rentalRepository;

    public List<StatItemDto> getRoomPopularityStats() {
        Map<String, Long> roomCounts = new HashMap<>();

        List<Booking> bookings = bookingRepository.findAll();
        for (Booking booking : bookings) {
            if (booking.getRoom() != null) {
                String roomTitle = booking.getRoom().getRoomTitle();
                roomCounts.put(roomTitle, roomCounts.getOrDefault(roomTitle, 0L) + 1);
            }
        }

        List<Rental> rentals = rentalRepository.findAll();
        for (Rental rental : rentals) {
            if (rental.getRoom() != null) {
                String roomTitle = rental.getRoom().getRoomTitle();
                roomCounts.put(roomTitle, roomCounts.getOrDefault(roomTitle, 0L) + 1);
            }
        }

        return roomCounts.entrySet().stream()
                .map(entry -> new StatItemDto(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    public List<StatItemDto> getServicePopularityStats() {
        Map<String, Long> serviceCounts = new HashMap<>();

        List<Booking> bookings = bookingRepository.findAll();
        for (Booking booking : bookings) {
            if (booking.getServices() != null) {
                for (Services service : booking.getServices()) {
                    String serviceName = service.getServiceName();
                    serviceCounts.put(serviceName, serviceCounts.getOrDefault(serviceName, 0L) + 1);
                }
            }
        }

        List<Rental> rentals = rentalRepository.findAll();
        for (Rental rental : rentals) {
            if (rental.getServices() != null) {
                for (Services service : rental.getServices()) {
                    String serviceName = service.getServiceName();
                    serviceCounts.put(serviceName, serviceCounts.getOrDefault(serviceName, 0L) + 1);
                }
            }
        }

        return serviceCounts.entrySet().stream()
                .map(entry -> new StatItemDto(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }
}
