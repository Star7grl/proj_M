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
    private final EmailService emailService;

    @Autowired
    public BookingService(BookingRepository bookingRepository,
                          UserService userService,
                          ServiceService serviceService,
                          ModelMapper modelMapper,
                          RoomService roomService,
                          EmailService emailService) {
        this.bookingRepository = bookingRepository;
        this.userService = userService;
        this.serviceService = serviceService;
        this.modelMapper = modelMapper;
        this.roomService = roomService;
        this.emailService = emailService;
    }

    @Transactional
    public Booking createBooking(BookingDto bookingDto) {
        Booking booking = modelMapper.map(bookingDto, Booking.class);

        User user = userService.findById(bookingDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден с ID: " + bookingDto.getUserId()));

        Room room = null;
        if (bookingDto.getRoomId() != null) {
            room = roomService.getRoomById(bookingDto.getRoomId())
                    .orElseThrow(() -> new ResourceNotFoundException("Комната не найдена с ID: " + bookingDto.getRoomId()));
            if (bookingDto.getCheckInDate().isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("Дата заезда не может быть в прошлом");
            }
            if (bookingDto.getCheckOutDate().isBefore(bookingDto.getCheckInDate()) || bookingDto.getCheckOutDate().isEqual(bookingDto.getCheckInDate())) {
                throw new IllegalArgumentException("Дата выезда должна быть позже даты заезда.");
            }
            if (isRoomBooked(room.getRoomId(), bookingDto.getCheckInDate(), bookingDto.getCheckOutDate())) {
                throw new RoomAlreadyBookedException("Комната уже забронирована на указанные даты");
            }
        }

        List<Services> servicesList = new ArrayList<>();
        if (bookingDto.getServiceIds() != null) {
            for (Long serviceId : bookingDto.getServiceIds()) {
                ServiceDto serviceDto = serviceService.findById(serviceId);
                Services serviceEntity = modelMapper.map(serviceDto, Services.class);
                servicesList.add(serviceEntity);
            }
        }

        booking.setUser(user);
        booking.setRoom(room);
        booking.setServices(servicesList);
        booking.setStatus("PENDING");
        booking.setTotalSum(calculateTotalSum(booking));

        Booking savedBooking = bookingRepository.save(booking);
        log.info("Бронирование ID {} успешно создано для пользователя {}. Статус: {}", savedBooking.getBookingId(), user.getUsername(), savedBooking.getStatus());

        try {
            emailService.sendBookingReceivedEmail(savedBooking);
        } catch (Exception e) {
            log.error("Не удалось отправить письмо о принятии бронирования ID {} для пользователя {}: {}", savedBooking.getBookingId(), user.getEmail(), e.getMessage(), e);
        }

        return savedBooking;
    }

    @Transactional
    public void updateBookingStatus(Long bookingId, String newStatus) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Бронирование не найдено c ID: " + bookingId));

        if (!isValidStatus(newStatus)) {
            throw new IllegalArgumentException("Недопустимый статус: " + newStatus);
        }

        String oldStatus = booking.getStatus();
        booking.setStatus(newStatus);
        Booking updatedBooking = bookingRepository.save(booking);
        log.info("Статус бронирования ID {} обновлен с {} на {}", bookingId, oldStatus, newStatus);

        try {
            if ("CONFIRMED".equals(newStatus) && !"CONFIRMED".equals(oldStatus)) {
                emailService.sendBookingConfirmedEmail(updatedBooking);
            } else if ("REJECTED".equals(newStatus) && !"REJECTED".equals(oldStatus)) {
                emailService.sendBookingRejectedEmail(updatedBooking);
            }
        } catch (Exception e) {
            log.error("Не удалось отправить письмо об обновлении статуса бронирования ID {} для пользователя {}: {}", updatedBooking.getBookingId(), updatedBooking.getUser().getEmail(), e.getMessage(), e);
        }
    }

    private boolean isValidStatus(String status) {
        return "PENDING".equals(status) || "CONFIRMED".equals(status) || "REJECTED".equals(status);
    }

    public Booking updateBooking(Booking bookingDetails) {
        Booking existingBooking = bookingRepository.findById(bookingDetails.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Бронирование не найдено c ID: " + bookingDetails.getBookingId()));

        User user = userService.findById(bookingDetails.getUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден c ID: " + bookingDetails.getUser().getId()));

        Room room = null;
        if (bookingDetails.getRoom() != null && bookingDetails.getRoom().getRoomId() != null) {
            room = roomService.getRoomById(bookingDetails.getRoom().getRoomId())
                    .orElseThrow(() -> new ResourceNotFoundException("Комната не найдена c ID: " + bookingDetails.getRoom().getRoomId()));
        }

        List<Services> servicesList = new ArrayList<>();
        if (bookingDetails.getServices() != null) {
            for (Services serviceEntity : bookingDetails.getServices()) {
                if (serviceEntity != null && serviceEntity.getServiceId() != null) {
                    ServiceDto serviceDto = serviceService.findById(serviceEntity.getServiceId());
                    servicesList.add(modelMapper.map(serviceDto, Services.class));
                }
            }
        }

        existingBooking.setUser(user);
        existingBooking.setRoom(room);
        existingBooking.setServices(servicesList);
        existingBooking.setCheckInDate(bookingDetails.getCheckInDate());
        existingBooking.setCheckOutDate(bookingDetails.getCheckOutDate());
        existingBooking.setStatus(bookingDetails.getStatus());
        existingBooking.setTotalSum(calculateTotalSum(existingBooking));

        Booking updatedBooking = bookingRepository.save(existingBooking);
        log.info("Бронирование ID {} успешно обновлено", updatedBooking.getBookingId());
        return updatedBooking;
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Optional<Booking> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }

    public List<Booking> getUserBookings(Long userId) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь с ID " + userId + " не найден."));
        return bookingRepository.findAll().stream() //TODO: заменить на findByUser(user) если есть такой метод в репо
                .filter(b -> b.getUser().getId().equals(userId))
                .toList();
    }

    public boolean isRoomBooked(Long roomId, LocalDate checkInDate, LocalDate checkOutDate) {
        log.debug("Проверка, забронирована ли комната {} на даты {} - {}", roomId, checkInDate, checkOutDate);
        List<Booking> overlappingBookings = bookingRepository.findOverlappingBookings(roomId, checkInDate, checkOutDate);
        if (!overlappingBookings.isEmpty()) {
            log.info("Комната {} забронирована на указанные даты. Существующие бронирования: {}", roomId, overlappingBookings.size());
            return true;
        }
        log.info("Комната {} свободна на указанные даты", roomId);
        return false;
    }

    @Transactional
    public void deleteBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Бронирование не найдено c ID: " + id));
        bookingRepository.delete(booking);
        log.info("Бронирование ID {} успешно удалено администратором", id);
    }

    // НОВЫЙ МЕТОД
    @Transactional
    public void deleteUserBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Бронирование не найдено c ID: " + bookingId));

        if (!booking.getUser().getId().equals(userId)) {
            log.warn("Пользователь ID {} попытался отменить чужое бронирование ID {}", userId, bookingId);
            throw new SecurityException("Вы можете отменять только свои бронирования.");
        }

        // Можно добавить проверки, например, отменять только PENDING или CONFIRMED
        // String currentStatus = booking.getStatus();
        // if (!("PENDING".equals(currentStatus) || "CONFIRMED".equals(currentStatus))) {
        //     log.warn("Попытка отменить бронирование ID {} со статусом {}", bookingId, currentStatus);
        //     throw new IllegalStateException("Бронирование со статусом '" + currentStatus + "' не может быть отменено.");
        // }

        bookingRepository.delete(booking);
        log.info("Бронирование ID {} успешно отменено пользователем ID {}", bookingId, userId);

        try {
            emailService.sendBookingCancelledByUserEmail(booking);
        } catch (Exception e) {
            log.error("Не удалось отправить письмо об отмене бронирования ID {} пользователем {}: {}", booking.getBookingId(), booking.getUser().getEmail(), e.getMessage(), e);
        }
    }

    private BigDecimal calculateTotalSum(Booking booking) {
        BigDecimal total = BigDecimal.ZERO;
        if (booking.getRoom() != null && booking.getRoom().getPrice() != null && booking.getCheckInDate() != null && booking.getCheckOutDate() != null) {
            long days = ChronoUnit.DAYS.between(booking.getCheckInDate(), booking.getCheckOutDate());
            if (days <= 0 && booking.getRoom().getPrice() > 0) { // Если бронь на 0 дней (только услуги) или некорректная дата, но комната выбрана
                days = 1; // Считаем как минимум 1 день, если комната есть
            }
            if (days > 0) { // Суммируем цену комнаты, только если есть дни проживания
                total = total.add(BigDecimal.valueOf(booking.getRoom().getPrice()).multiply(BigDecimal.valueOf(days)));
            }
        }
        if (booking.getServices() != null) {
            for (Services service : booking.getServices()) {
                if (service.getServicePrice() != null) {
                    total = total.add(BigDecimal.valueOf(service.getServicePrice()));
                }
            }
        }
        return total;
    }
}
