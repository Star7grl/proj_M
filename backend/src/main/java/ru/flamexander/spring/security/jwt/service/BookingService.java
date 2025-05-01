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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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

        Room room = null;
        if (bookingDto.getRoomId() != null) {
            room = roomService.getRoomById(bookingDto.getRoomId())
                    .orElseThrow(() -> new ResourceNotFoundException("Комната не найдена"));
            if (bookingDto.getCheckInDate().isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("Дата заезда не может быть в прошлом");
            }
            if (bookingDto.getCheckOutDate().isBefore(bookingDto.getCheckInDate())) {
                throw new IllegalArgumentException("Дата выезда не может быть раньше даты заезда");
            }
            if (isRoomBooked(room.getRoomId(), bookingDto.getCheckInDate(), bookingDto.getCheckOutDate())) {
                throw new RoomAlreadyBookedException("Комната уже забронирована на указанные даты");
            }
        }

        List<Services> services = new ArrayList<>();
        if (bookingDto.getServiceIds() != null) {
            for (Long serviceId : bookingDto.getServiceIds()) {
                ServiceDto serviceDto = serviceService.findById(serviceId);
                Services service = modelMapper.map(serviceDto, Services.class);
                services.add(service);
            }
        }

        booking.setUser(user);
        booking.setRoom(room);
        booking.setServices(services);
        booking.setStatus("PENDING");
        booking.setTotalSum(calculateTotalSum(booking));

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

        Room room = null;
        if (booking.getRoom() != null) {
            room = roomService.getRoomById(booking.getRoom().getRoomId())
                    .orElseThrow(() -> new ResourceNotFoundException("Комната не найдена"));
        }

        List<Services> services = new ArrayList<>();
        if (booking.getServices() != null) {
            for (Services service : booking.getServices()) {
                ServiceDto serviceDto = serviceService.findById(service.getServiceId());
                services.add(modelMapper.map(serviceDto, Services.class));
            }
        }

        booking.setUser(user);
        booking.setRoom(room);
        booking.setServices(services);

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

    private BigDecimal calculateTotalSum(Booking booking) {
        BigDecimal total = BigDecimal.ZERO;
        if (booking.getRoom() != null) {
            long days = ChronoUnit.DAYS.between(booking.getCheckInDate(), booking.getCheckOutDate());
            total = total.add(BigDecimal.valueOf(booking.getRoom().getPrice()).multiply(BigDecimal.valueOf(days)));
        }
        if (booking.getServices() != null) {
            for (Services service : booking.getServices()) {
                total = total.add(BigDecimal.valueOf(service.getServicePrice()));
            }
        }
        return total;
    }
}