package ru.flamexander.spring.security.jwt.controllers;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.flamexander.spring.security.jwt.dtos.BookingDto;
import ru.flamexander.spring.security.jwt.entities.Booking;
import ru.flamexander.spring.security.jwt.entities.Services;
import ru.flamexander.spring.security.jwt.exceptions.ResourceNotFoundException;
import ru.flamexander.spring.security.jwt.service.BookingService;
import ru.flamexander.spring.security.jwt.exceptions.RoomAlreadyBookedException; ///
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private final ModelMapper modelMapper;

    @GetMapping
    public List<Booking> getAllBookings() {
        return bookingService.getAllBookings();
    }

    @GetMapping("/{id}")
    public Booking getBookingById(@PathVariable Long id) {
        return bookingService.getBookingById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
    }

    //localhost:8080/api/bookings/add
    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public Booking createBooking(@RequestBody BookingDto bookingDto) {
        return bookingService.createBooking(bookingDto);
    }

    @PutMapping("/update/{id}")
    public Booking updateBooking(@PathVariable Long id, @RequestBody BookingDto bookingDto) {
        Booking booking = modelMapper.map(bookingDto, Booking.class);
        booking.setBookingId(id);
        return bookingService.updateBooking(booking);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
    }

}









