package ru.flamexander.spring.security.jwt.controllers;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication; // <<< НОВЫЙ ИМПОРТ
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.flamexander.spring.security.jwt.configs.CustomUserDetails; // <<< НОВЫЙ ИМПОРТ
import ru.flamexander.spring.security.jwt.dtos.BookingDto;
import ru.flamexander.spring.security.jwt.entities.Booking;
import ru.flamexander.spring.security.jwt.entities.Rental;
import ru.flamexander.spring.security.jwt.exceptions.ResourceNotFoundException;
import ru.flamexander.spring.security.jwt.exceptions.RoomAlreadyBookedException;
import ru.flamexander.spring.security.jwt.repositories.BookingRepository;
import ru.flamexander.spring.security.jwt.repositories.RentalRepository;
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
@CrossOrigin(origins = "http://localhost:5174") // Для CorsFilter это может быть не нужно, если CorsConfigSource настроен
public class BookingController {
    private final BookingService bookingService;
    private final ModelMapper modelMapper;
    private final BookingRepository bookingRepository; // Не используется напрямую, можно удалить если только через сервис
    private final RentalRepository rentalRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')") // Защищаем получение всех бронирований
    public List<Booking> getAllBookings() {
        return bookingService.getAllBookings();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()") // Доступно аутентифицированным (логика проверки прав в сервисе)
    public ResponseEntity<Booking> getBookingById(@PathVariable Long id) {
        return bookingService.getBookingById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/add")
    @PreAuthorize("isAuthenticated()") // Только аутентифицированные пользователи могут бронировать
    public ResponseEntity<?> createBooking(@Valid @RequestBody BookingDto bookingDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body("Некорректные данные для бронирования");
        }
        try {
            Booking booking = bookingService.createBooking(bookingDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(booking);
        } catch (RoomAlreadyBookedException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (IllegalArgumentException | ResourceNotFoundException e) { // Объединяем обработку
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Произошла ошибка при бронировании");
        }
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Только админ может обновлять любое бронирование
    public Booking updateBooking(@PathVariable Long id, @RequestBody BookingDto bookingDto) {
        Booking booking = modelMapper.map(bookingDto, Booking.class);
        booking.setBookingId(id); // Убедимся, что ID установлен для обновления
        return bookingService.updateBooking(booking);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Только админ может удалять любое бронирование
    public ResponseEntity<?> deleteBooking(@PathVariable Long id) {
        try {
            bookingService.deleteBooking(id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Произошла ошибка при удалении бронирования");
        }
    }

    // НОВЫЙ МЕТОД для отмены бронирования пользователем
    @DeleteMapping("/user/delete/{bookingId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteUserBooking(@PathVariable Long bookingId, Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Пользователь не аутентифицирован должным образом."));
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUser().getId();

        try {
            bookingService.deleteUserBooking(bookingId, userId);
            return ResponseEntity.ok().body(Map.of("message", "Бронирование успешно отменено"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            // Логирование ошибки здесь было бы полезно
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Произошла ошибка при отмене бронирования: " + e.getMessage()));
        }
    }


    @GetMapping("/user/{userId}")
    @PreAuthorize("isAuthenticated()") // Пользователь может запрашивать только свои бронирования или админ - любые
    public ResponseEntity<?> getUserBookings(@PathVariable Long userId, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long currentUserId = userDetails.getUser().getId();
        String userRole = userDetails.getAuthorities().iterator().next().getAuthority();

        if (!currentUserId.equals(userId) && !"ROLE_ADMIN".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Вы можете просматривать только свои бронирования.");
        }
        return ResponseEntity.ok(bookingService.getUserBookings(userId));
    }

    @GetMapping("/{roomId}/booked-dates")
    public ResponseEntity<Map<String, List<LocalDate>>> getBookedDates(@PathVariable Long roomId) {
        List<Booking> bookings = bookingService.getAllBookings(); // Это может быть неэффективно, если много броней
        List<Rental> rentals = rentalRepository.findAll();

        List<LocalDate> bookedDates = bookings.stream()
                .filter(booking -> booking.getRoom() != null && booking.getRoom().getRoomId().equals(roomId))
                .flatMap(booking -> {
                    List<LocalDate> dates = new ArrayList<>();
                    LocalDate startDate = booking.getCheckInDate();
                    LocalDate endDate = booking.getCheckOutDate();
                    if (startDate == null || endDate == null) return dates.stream(); // Проверка на null
                    LocalDate currentDate = startDate;
                    while (!currentDate.isAfter(endDate)) {
                        dates.add(currentDate);
                        currentDate = currentDate.plusDays(1);
                    }
                    return dates.stream();
                })
                .collect(Collectors.toList());

        List<LocalDate> rentalDates = rentals.stream()
                .filter(rental -> rental.getRoom() != null && rental.getRoom().getRoomId().equals(roomId))
                .flatMap(rental -> {
                    List<LocalDate> dates = new ArrayList<>();
                    LocalDate startDate = rental.getCheckInDate();
                    LocalDate endDate = rental.getCheckOutDate();
                    if (startDate == null || endDate == null) return dates.stream(); // Проверка на null
                    LocalDate currentDate = startDate;
                    while (!currentDate.isAfter(endDate)) {
                        dates.add(currentDate);
                        currentDate = currentDate.plusDays(1);
                    }
                    return dates.stream();
                })
                .collect(Collectors.toList());

        List<LocalDate> allBookedDates = new ArrayList<>(bookedDates);
        allBookedDates.addAll(rentalDates);

        LocalDate today = LocalDate.now();
        List<LocalDate> pastDates = new ArrayList<>();
        LocalDate date = today.minusYears(1); // Ограничим прошлые даты одним годом для производительности
        while (!date.isAfter(today)) {
            pastDates.add(date);
            date = date.plusDays(1);
        }

        Map<String, List<LocalDate>> response = new HashMap<>();
        response.put("booked", allBookedDates.stream().distinct().collect(Collectors.toList())); // Убираем дубликаты
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
