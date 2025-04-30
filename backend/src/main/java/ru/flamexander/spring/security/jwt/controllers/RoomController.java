package ru.flamexander.spring.security.jwt.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.flamexander.spring.security.jwt.dtos.RoomDto;
import ru.flamexander.spring.security.jwt.entities.Room;
import ru.flamexander.spring.security.jwt.repositories.RoomRepository;
import ru.flamexander.spring.security.jwt.service.RoomService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/rooms")
@CrossOrigin(origins = "http://localhost:5174")
public class RoomController {

    private final RoomService roomService;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    public ResponseEntity<Page<Room>> getAllRooms(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "9") int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Room> rooms = roomService.getAllRooms(pageable);
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/admin")
    public ResponseEntity<Page<Room>> getAllRoomsAdmin(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Room> rooms = roomService.getAllRooms(pageable);
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Room> getRoomById(@PathVariable Long id) {
        Optional<Room> room = roomService.getRoomById(id);
        return room.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/add")
    public ResponseEntity<Room> createRoom(@RequestBody RoomDto roomDto) {
        Room createdRoom = roomService.createRoom(roomDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRoom);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Room> updateRoom(@PathVariable Long id, @RequestBody RoomDto roomDto) {
        try {
            Room updatedRoom = roomService.updateRoom(id, roomDto);
            return ResponseEntity.ok(updatedRoom);
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        if (!roomService.getRoomById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/searchTitle")
    public ResponseEntity<List<Room>> searchRooms(@RequestParam(required = false) String title) {
        if (title != null && !title.isEmpty()) {
            return ResponseEntity.ok(roomService.searchByTitle(title));
        }
        return ResponseEntity.ok(roomService.getAllRooms());
    }

    @GetMapping("/searchPrice")
    public ResponseEntity<List<Room>> searchRooms(
            @RequestParam(required = false) String roomTitle,
            @RequestParam(required = false, defaultValue = "0") double minPrice,
            @RequestParam(required = false, defaultValue = "100000") double maxPrice) {
        return ResponseEntity.ok(roomService.searchRooms(roomTitle, minPrice, maxPrice));
    }

    @GetMapping("/available")
    public ResponseEntity<List<Room>> getAvailableRooms(
            @RequestParam String checkInDate,
            @RequestParam String checkOutDate) {
        LocalDate checkIn = LocalDate.parse(checkInDate);
        LocalDate checkOut = LocalDate.parse(checkOutDate);
        List<Room> availableRooms = roomService.getAvailableRooms(checkIn, checkOut);
        return ResponseEntity.ok(availableRooms);
    }
}