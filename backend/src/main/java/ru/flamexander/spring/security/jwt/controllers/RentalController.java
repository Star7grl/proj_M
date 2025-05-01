package ru.flamexander.spring.security.jwt.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.flamexander.spring.security.jwt.entities.Rental;
import ru.flamexander.spring.security.jwt.entities.Services;
import ru.flamexander.spring.security.jwt.entities.User;
import ru.flamexander.spring.security.jwt.repositories.RentalRepository;
import ru.flamexander.spring.security.jwt.service.ServiceService;
import ru.flamexander.spring.security.jwt.service.UserService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rentals")
public class RentalController {

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ServiceService serviceService;

    @GetMapping
    @PreAuthorize("hasRole('HOSTES')")
    public ResponseEntity<List<Rental>> getRentalsForHostes() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        User hostes = userService.findByUsername(username).orElse(null);
        if (hostes == null) {
            return ResponseEntity.badRequest().build();
        }

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

        rental.setHostesId(hostes.getId());
        rental.setCheckInDate(LocalDate.now());

        if (rental.getServiceIds() != null) {
            List<Services> services = rental.getServiceIds().stream()
                    .map(id -> serviceService.findById(id))
                    .map(dto -> new Services(dto.getServiceId(), dto.getServiceName(), dto.getServicePrice(), dto.getImageUrl()))
                    .collect(Collectors.toList());
            rental.setServices(services);
        }

        Rental savedRental = rentalRepository.save(rental);
        return ResponseEntity.ok(savedRental);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('HOSTES', 'ADMIN')")
    public ResponseEntity<List<Rental>> getAllRentals() {
        List<Rental> rentals = rentalRepository.findAll();
        return ResponseEntity.ok(rentals);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('HOSTES')")
    public ResponseEntity<Rental> updateRental(@PathVariable Long id, @RequestBody Rental updatedRental) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        User hostes = userService.findByUsername(username).orElse(null);
        if (hostes == null) {
            return ResponseEntity.badRequest().build();
        }

        Optional<Rental> optionalRental = rentalRepository.findById(id);
        if (!optionalRental.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Rental rental = optionalRental.get();
        if (!rental.getHostesId().equals(hostes.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Обновляем поля
        rental.setVisitorFirstName(updatedRental.getVisitorFirstName());
        rental.setVisitorLastName(updatedRental.getVisitorLastName());
        rental.setVisitorPhone(updatedRental.getVisitorPhone());
        rental.setCheckInDate(updatedRental.getCheckInDate());
        rental.setCheckOutDate(updatedRental.getCheckOutDate());
        // Если нужно обновить комнату или услуги, добавь логику здесь

        Rental savedRental = rentalRepository.save(rental);
        return ResponseEntity.ok(savedRental);
    }
}