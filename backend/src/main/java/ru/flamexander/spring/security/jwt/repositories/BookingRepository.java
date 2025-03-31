package ru.flamexander.spring.security.jwt.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.flamexander.spring.security.jwt.entities.Booking;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByRoom_RoomIdAndCheckInDateGreaterThanEqualAndCheckOutDateLessThanEqual(
            Long roomId,
            LocalDate checkInDate,
            LocalDate checkOutDate
    );
}
