package ru.flamexander.spring.security.jwt.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.flamexander.spring.security.jwt.entities.Room;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    // Поиск комнат по типу
    List<Room> findByRoomType(String roomType);

    // Поиск комнат по частичному совпадению названия (без учета регистра)
    List<Room> findByRoomTitleContainingIgnoreCase(String roomTitle);

    Page<Room> findByPriceBetween(double minPrice,
                                  double maxPrice,
                                  Pageable pageable);

    Page<Room> findByRoomTitleContainingAndPriceBetween(
            String roomTitle,
            double minPrice,
            double maxPrice,
            Pageable pageable);

    // Поиск комнат с определенным статусом (например, "AVAILABLE") с пагинацией
    //Page<Room> findByStatusNot(String status, Pageable pageable);

    // Поиск всех доступных комнат (статус "AVAILABLE")
    //List<Room> findByStatus(String available);
}