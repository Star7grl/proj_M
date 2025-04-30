package ru.flamexander.spring.security.jwt.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.flamexander.spring.security.jwt.entities.Booking;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

//    Запрос ищет бронирования для конкретной комнаты (roomId), которые пересекаются по датам с заданным интервалом (checkInDate - checkOutDate).
    @Query("SELECT b FROM Booking b WHERE b.room.roomId = :roomId " +
            "AND ((:checkInDate BETWEEN b.checkInDate AND b.checkOutDate) " +
            "OR (:checkOutDate BETWEEN b.checkInDate AND b.checkOutDate) " +
            "OR (b.checkInDate BETWEEN :checkInDate AND :checkOutDate) " +
            "OR (b.checkOutDate BETWEEN :checkInDate AND :checkOutDate))")
    List<Booking> findOverlappingBookings(Long roomId, LocalDate checkInDate, LocalDate checkOutDate);

//    Запрос возвращает все бронирования по конкретному roomId
    @Query("SELECT b FROM Booking b WHERE b.room.roomId = :roomId")
    List<Booking> findAllByRoomId(Long roomId);

    // Метод для проверки активных бронирований пользователя
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.user.id = :userId AND b.status = 'ACTIVE'")
    long countActiveBookingsByUserId(Long userId);
}