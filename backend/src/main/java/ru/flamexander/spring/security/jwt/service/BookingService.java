package ru.flamexander.spring.security.jwt.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.flamexander.spring.security.jwt.dtos.BookingDto;
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
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
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

    @Transactional
    public Booking createBooking(BookingDto bookingDto) {
        Booking booking = modelMapper.map(bookingDto, Booking.class);

        User user = userService.findById(bookingDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));

        Optional<Room> roomOptional = roomService.getRoomById(bookingDto.getRoomId());
        Room room = roomOptional.orElseThrow(() -> new ResourceNotFoundException("Комната не найдена"));

        if (bookingDto.getCheckInDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Дата заезда не может быть в прошлом");
        }
        if (bookingDto.getCheckOutDate().isBefore(bookingDto.getCheckInDate())) {
            throw new IllegalArgumentException("Дата выезда не может быть раньше даты заезда");
        }
        if (isRoomBooked(room.getRoomId(), bookingDto.getCheckInDate(), bookingDto.getCheckOutDate())) {
            throw new RoomAlreadyBookedException("Комната уже забронирована на указанные даты");
        }

        Services service = null;
        if (bookingDto.getServiceId() != null) {
            ServiceDto serviceDto = serviceService.findById(bookingDto.getServiceId());
            service = modelMapper.map(serviceDto, Services.class);
        }

        booking.setUser(user);
        booking.setRoom(room);
        booking.setService(service);
        booking.setStatus("PENDING");

        Booking savedBooking = bookingRepository.save(booking);
        return savedBooking;
    }

    @Transactional
    public void updateBookingStatus(Long bookingId, String status) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Бронирование не найдено"));
        if (!isValidStatus(status)) {
            throw new IllegalArgumentException("Недопустимый статус: " + status);
        }
        booking.setStatus(status);
        bookingRepository.save(booking);
    }

    private boolean isValidStatus(String status) {
        return "PENDING".equals(status) || "CONFIRMED".equals(status) || "REJECTED".equals(status);
    }

    public Booking updateBooking(Booking booking) {
        Booking existingBooking = bookingRepository.findById(booking.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Бронирование не найдено"));

        User user = userService.findById(booking.getUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));

        Optional<Room> roomOptional = roomService.getRoomById(booking.getRoom().getRoomId());
        Room room = roomOptional.orElseThrow(() -> new ResourceNotFoundException("Комната не найдена"));

        Services service = null;
        if (booking.getService() != null) {
            ServiceDto serviceDto = serviceService.findById(booking.getService().getServiceId());
            service = modelMapper.map(serviceDto, Services.class);
        }

        booking.setUser(user);
        booking.setRoom(room);
        booking.setService(service);

        return bookingRepository.save(booking);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Optional<Booking> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }

    public List<Booking> getUserBookings(Long userId) {
        return bookingRepository.findAll().stream()
                .filter(b -> b.getUser().getId().equals(userId))
                .toList();
    }

    public boolean isRoomBooked(Long roomId, LocalDate checkInDate, LocalDate checkOutDate) {
        log.info("Проверка, забронирована ли комната {} на даты {} - {}", roomId, checkInDate, checkOutDate);
        List<Booking> overlappingBookings = bookingRepository.findOverlappingBookings(roomId, checkInDate, checkOutDate);
        if (!overlappingBookings.isEmpty()) {
            log.info("Комната {} забронирована на указанные даты", roomId);
            return true;
        }
        log.info("Комната {} свободна на указанные даты", roomId);
        return false;
    }

    @Transactional
    public void deleteBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Бронирование не найдено"));
        bookingRepository.delete(booking);
    }
}