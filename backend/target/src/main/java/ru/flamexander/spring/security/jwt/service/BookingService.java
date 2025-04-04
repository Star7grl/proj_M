package ru.flamexander.spring.security.jwt.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.flamexander.spring.security.jwt.dtos.BookingDto;
import ru.flamexander.spring.security.jwt.dtos.RoomDto;
import ru.flamexander.spring.security.jwt.dtos.ServiceDto;
import ru.flamexander.spring.security.jwt.entities.Booking;
import ru.flamexander.spring.security.jwt.entities.Room;
import ru.flamexander.spring.security.jwt.entities.Services;
import ru.flamexander.spring.security.jwt.entities.User;
import ru.flamexander.spring.security.jwt.exceptions.ResourceNotFoundException;
import ru.flamexander.spring.security.jwt.exceptions.RoomAlreadyBookedException;
import ru.flamexander.spring.security.jwt.repositories.BookingRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ServiceService serviceService;
    private final ModelMapper modelMapper;
    private final RoomService roomService;

    @Autowired
    public BookingService(BookingRepository bookingRepository,
                          UserService userService,
                          ServiceService serviceService,
                          ModelMapper modelMapper,
                          RoomService roomService) {
        this.bookingRepository = bookingRepository;
        this.userService = userService;
        this.serviceService = serviceService;
        this.modelMapper = modelMapper;
        this.roomService = roomService;
    }

    // В файле BookingService.java

    public Booking createBooking(BookingDto bookingDto) {
        Booking booking = modelMapper.map(bookingDto, Booking.class);

        // Проверяем существование пользователя
        User user = userService.findById(bookingDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));

        // Проверяем существование услуги
        ServiceDto serviceDto = serviceService.findById(bookingDto.getServiceId());
        Services service = modelMapper.map(serviceDto, Services.class);

        // Проверяем существование комнаты и преобразуем RoomDto в Room
        Optional<Room> roomOptional = roomService.findById(bookingDto.getRoomId());
        Room room = roomOptional.orElseThrow(() ->
                new ResourceNotFoundException("Комната не найдена"));

        // Проверка на занятость комнаты
        if (isRoomBooked(room.getRoomId(), bookingDto.getCheckInDate(), bookingDto.getCheckOutDate())) {
            throw new RoomAlreadyBookedException("Room is already booked for the specified dates.");
        }

        booking.setUser(user);
        booking.setService(service);
        booking.setRoom(room);

        return bookingRepository.save(booking);
    }


    public Booking updateBooking(Booking booking) {
        // Проверяем существование бронирования
        Booking existingBooking = bookingRepository.findById(booking.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Бронироавние не найдено"));

        // Проверяем существование пользователя
        User user = userService.findById(booking.getUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));

        // Проверяем существование услуги
        ServiceDto serviceDto = serviceService.findById(booking.getService().getServiceId());
        Services service = modelMapper.map(serviceDto, Services.class);

        // Проверяем существование комнаты
        Optional<Room> roomOptional = roomService.findById(booking.getRoom().getRoomId());
        Room room = roomOptional.orElseThrow(() ->
                new ResourceNotFoundException("Комната не найдена"));

        booking.setUser(user);
        booking.setService(service);
        booking.setRoom(room);

        return bookingRepository.save(booking);
    }


    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Optional<Booking> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }

    public boolean isRoomBooked(Long roomId, LocalDate checkInDate, LocalDate checkOutDate) {
        return !bookingRepository
                .findAllByRoom_RoomIdAndCheckInDateGreaterThanEqualAndCheckOutDateLessThanEqual(
                        roomId,
                        checkInDate,
                        checkOutDate
                )
                .isEmpty();
    }


    // В файле BookingController.java
    // В файле BookingService.java
    public void deleteBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Бронирование не найдено"));
        bookingRepository.delete(booking);
    }



}
