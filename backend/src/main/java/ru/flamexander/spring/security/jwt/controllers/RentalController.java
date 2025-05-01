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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rentals")
public class RentalController {

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('HOSTES')")
    public ResponseEntity<List<Rental>> getRentalsForHostes() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        User hostes = userService.findByUsername(username).orElse(null);
        if (hostes == null) {
            return ResponseEntity.badRequest().build();
        }

        // Фильтруем аренды по hostesId
        List<Rental> rentals = rentalRepository.findAll().stream()
                .filter(rental -> rental.getHostesId() != null && rental.getHostesId().equals(hostes.getId()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(rentals);
    }

    @PostMapping
    @PreAuthorize("hasRole('HOSTES')")
    public ResponseEntity<Rental> createRental(@RequestBody Rental rental) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        User hostes = userService.findByUsername(username).orElse(null);
        if (hostes == null) {
            return ResponseEntity.badRequest().build();
        }

        rental.setHostesId(hostes.getId()); // Сохраняем ID хостеса
        rental.setCheckInDate(LocalDate.now());
        Rental savedRental = rentalRepository.save(rental);
        return ResponseEntity.ok(savedRental);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('HOSTES', 'ADMIN')")
    public ResponseEntity<List<Rental>> getAllRentals() {
        List<Rental> rentals = rentalRepository.findAll();
        return ResponseEntity.ok(rentals);
    }
}