package ru.flamexander.spring.security.jwt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.flamexander.spring.security.jwt.dtos.RoomDto;
import ru.flamexander.spring.security.jwt.entities.Room;
import ru.flamexander.spring.security.jwt.entities.RoomImage;
import ru.flamexander.spring.security.jwt.repositories.BookingRepository;
import ru.flamexander.spring.security.jwt.repositories.RoomRepository;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoomService {
    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;

    @Autowired
    public RoomService(RoomRepository roomRepository, BookingRepository bookingRepository) {
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
    }

    public Page<Room> getAllRooms(Pageable pageable) {
        return roomRepository.findAll(pageable);
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public Optional<Room> getRoomById(Long id) {
        return roomRepository.findById(id);
    }

    public Room createRoom(RoomDto roomDto) {
        Room room = new Room();
        room.setRoomTitle(roomDto.getRoomTitle());
        room.setRoomType(roomDto.getRoomType());
        room.setDescription(roomDto.getDescription());
        room.setPrice(roomDto.getPrice());
        room.setStatus(roomDto.getStatus());
        for (String url : roomDto.getImageUrls()) {
            RoomImage image = new RoomImage();
            image.setRoom(room);
            image.setImageUrl(url);
            room.getImages().add(image);
        }
        return roomRepository.save(room);
    }

    @Transactional
    public Room updateRoom(Room room) {
        return roomRepository.save(room); // Обновление комнаты, включая статус
    }

    @Transactional
    public Room updateRoom(Long id, RoomDto roomDto) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Не найдена комната с айдишником: " + id));
        room.setRoomTitle(roomDto.getRoomTitle());
        room.setRoomType(roomDto.getRoomType());
        room.setDescription(roomDto.getDescription());
        room.setPrice(roomDto.getPrice());
        room.setStatus(roomDto.getStatus());
        // Очищаем текущие изображения
        room.getImages().clear();
        // Добавляем новые изображения
        for (String url : roomDto.getImageUrls()) {
            RoomImage image = new RoomImage();
            image.setRoom(room);
            image.setImageUrl(url);
            room.getImages().add(image);
        }
        return roomRepository.save(room);
    }

    @Transactional
    public void deleteRoom(Long id) {
        // Сначала удаляем все бронирования, связанные с этой комнатой
        bookingRepository.findAllByRoomId(id)
                .forEach(booking -> bookingRepository.delete(booking));

        // Затем удаляем саму комнату
        roomRepository.deleteById(id);
    }

    public List<Room> searchByTitle(String roomTitle) {
        return roomRepository.findByRoomTitleContainingIgnoreCase(roomTitle);
    }

    public List<Room> searchRooms(String roomTitle, double minPrice, double maxPrice) {
        if (roomTitle != null && !roomTitle.isEmpty()) {
            return roomRepository.findByRoomTitleContainingAndPriceBetween(roomTitle, minPrice, maxPrice);
        } else {
            return roomRepository.findByPriceBetween(minPrice, maxPrice);
        }
    }

    public List<Room> getAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate) {
        return getAllRooms().stream()
                .filter(room -> isRoomAvailable(room.getRoomId(), checkInDate, checkOutDate))
                .collect(Collectors.toList());
    }

    private boolean isRoomAvailable(Long roomId, LocalDate checkInDate, LocalDate checkOutDate) {
        return bookingRepository.findOverlappingBookings(roomId, checkInDate, checkOutDate).isEmpty();
    }
}