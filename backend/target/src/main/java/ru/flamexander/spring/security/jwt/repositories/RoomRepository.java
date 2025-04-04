package ru.flamexander.spring.security.jwt.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.flamexander.spring.security.jwt.entities.Room;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByRoomType(String roomType);
    // Добавляем новый метод
    List<Room> findByRoomTitleContainingIgnoreCase(String roomTitle);

    List<Room> findByRoomTitleContainingAndPriceBetween(String roomTitle, double minPrice, double maxPrice);

    List<Room> findByPriceBetween(double minPrice, double maxPrice);
}