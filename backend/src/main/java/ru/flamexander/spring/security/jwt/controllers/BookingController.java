package ru.flamexander.spring.security.jwt.controllers;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.flamexander.spring.security.jwt.dtos.BookingDto;
import ru.flamexander.spring.security.jwt.entities.Booking;
import ru.flamexander.spring.security.jwt.exceptions.ResourceNotFoundException;
import ru.flamexander.spring.security.jwt.exceptions.RoomAlreadyBookedException;
import ru.flamexander.spring.security.jwt.repositories.BookingRepository;
import ru.flamexander.spring.security.jwt.service.BookingService;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5174")
public class BookingController {
    private final BookingService bookingService;
    private final ModelMapper modelMapper;
    private final BookingRepository bookingRepository;

    @GetMapping
    public List<Booking> getAllBookings() {
        return bookingService.getAllBookings();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable Long id) {
        return bookingService.getBookingById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/add")
    public ResponseEntity<?> createBooking(@Valid @RequestBody BookingDto bookingDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body("Некорректные данные для бронирования");
        }
        try {
            Booking booking = bookingService.createBooking(bookingDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(booking);
        } catch (RoomAlreadyBookedException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Произошла ошибка при бронировании");
        }
    }

    @PutMapping("/update/{id}")
    public Booking updateBooking(@PathVariable Long id, @RequestBody BookingDto bookingDto) {
        Booking booking = modelMapper.map(bookingDto, Booking.class);
        booking.setBookingId(id);
        return bookingService.updateBooking(booking);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteBooking(@PathVariable Long id) {
        try {
            bookingService.deleteBooking(id);
            return ResponseEntity.noContent().build(); // 204 No Content при успехе
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // 404 если не найдено
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Произошла ошибка при удалении бронирования"); // 500 при ошибке
        }
    }

    @GetMapping("/user/{userId}")
    public List<Booking> getUserBookings(@PathVariable Long userId) {
        return bookingService.getUserBookings(userId);
    }

    @GetMapping("/{roomId}/booked-dates")
    public ResponseEntity<Map<String, List<LocalDate>>> getBookedDates(@PathVariable Long roomId) {
        List<Booking> bookings = bookingService.getAllBookings();
        List<LocalDate> bookedDates = bookings.stream()
                .filter(booking -> booking.getRoom().getRoomId().equals(roomId))
                .flatMap(booking -> {
                    List<LocalDate> dates = new ArrayList<>();
                    LocalDate startDate = booking.getCheckInDate();
                    LocalDate endDate = booking.getCheckOutDate();
                    LocalDate currentDate = startDate;
                    while (!currentDate.isAfter(endDate)) {
                        dates.add(currentDate);
                        currentDate = currentDate.plusDays(1);
                    }
                    return dates.stream();
                })
                .collect(Collectors.toList());

        LocalDate today = LocalDate.now();
        List<LocalDate> pastDates = new ArrayList<>();
        LocalDate date = today.minusDays(365); // Прошедшие даты за год (можно настроить)
        while (!date.isAfter(today)) {
            pastDates.add(date);
            date = date.plusDays(1);
        }

        Map<String, List<LocalDate>> response = new HashMap<>();
        response.put("booked", bookedDates);
        response.put("past", pastDates);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/updateStatus/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateBookingStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            bookingService.updateBookingStatus(id, status);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}