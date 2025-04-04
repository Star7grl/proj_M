package ru.flamexander.spring.security.jwt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.flamexander.spring.security.jwt.dtos.RoomDto;
import ru.flamexander.spring.security.jwt.entities.Room;
import ru.flamexander.spring.security.jwt.repositories.RoomRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class RoomService {
    private RoomRepository roomRepository;

    @Autowired
    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public Optional<Room> getRoomById(Long id) {
        return roomRepository.findById(id);
    }

    public Room createRoom(RoomDto roomDto) {
        Room room = new Room();
        room.setRoomTitle(roomDto.getRoomTitle()); // Добавьте эту строку
        room.setRoomType(roomDto.getRoomType());
        room.setDescription(roomDto.getDescription());
        room.setPrice(roomDto.getPrice());
        room.setStatus(roomDto.getStatus());
        return roomRepository.save(room);
    }


    @Autowired
    public void setRoomRepository(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public List<Room> findAll() {
        return roomRepository.findAll();
    }

    public Optional<Room> findById(Long id) {
        return roomRepository.findById(id);
    }

    public List<Room> findByRoomType(String roomType) {
        return roomRepository.findByRoomType(roomType);
    }

    @Transactional
    public Room createNewRoom(RoomDto roomDto) {
        Room room = new Room();
        room.setRoomType(roomDto.getRoomType());
        room.setDescription(roomDto.getDescription());
        room.setPrice(roomDto.getPrice());
        room.setStatus(roomDto.getStatus());
        return roomRepository.save(room);
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
        return roomRepository.save(room);
    }

    @Transactional
    public void deleteRoom(Long id) {
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



}