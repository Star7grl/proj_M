// RentalController.java
package ru.flamexander.spring.security.jwt.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.flamexander.spring.security.jwt.entities.Rental;
import ru.flamexander.spring.security.jwt.entities.User;
import ru.flamexander.spring.security.jwt.repositories.RentalRepository;
import ru.flamexander.spring.security.jwt.service.UserService;

import java.time.LocalDate;
import java.util.List;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rentals")
public class RentalController {

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private UserService userService; // Добавьте UserService

    @GetMapping
    @PreAuthorize("hasRole('HOSTES')")
    public ResponseEntity<List<Rental>> getRentalsForHostes() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName(); // Получаем имя пользователя

        User hostes = userService.findByUsername(username).orElse(null);
        if (hostes == null) {
            return ResponseEntity.badRequest().build(); // Или другой подходящий ответ
        }

        List<Rental> rentals = rentalRepository.findAll().stream()
                .filter(rental -> rental.getRoom().getRoomId().equals(hostes.getId()))
                .collect(Collectors.toList()); // Получаем все аренды
        return ResponseEntity.ok(rentals);
    }

    @PostMapping
    @PreAuthorize("hasRole('HOSTES')")
    public ResponseEntity<Rental> createRental(@RequestBody Rental rental) {
        rental.setCheckInDate(LocalDate.now()); // Текущая дата заезда
        Rental savedRental = rentalRepository.save(rental);
        return ResponseEntity.ok(savedRental);
    }
}
