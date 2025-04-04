package ru.flamexander.spring.security.jwt.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.flamexander.spring.security.jwt.dtos.RoomDto;
import ru.flamexander.spring.security.jwt.entities.Room;
import ru.flamexander.spring.security.jwt.service.RoomService;

import java.util.List;
import java.util.Optional;

@RestController // Аннотация, обозначающая, что это REST контроллер
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    @Autowired // Автоматическое внедрение зависимости
    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping // Обработчик GET-запроса для получения всех номеров
    public ResponseEntity<List<Room>> getAllRooms() {
        List<Room> rooms = roomService.getAllRooms();
        return ResponseEntity.ok(rooms); // Возвращает список номеров с кодом 200 OK
    }

    @GetMapping("/{id}")
    public ResponseEntity<Room> getRoomById(@PathVariable Long id) { // @PathVariable извлекает ID из пути
        Optional<Room> room = roomService.getRoomById(id);
        return room.map(ResponseEntity::ok) // Если номер найден, возвращает его с кодом 200 OK
                .orElse(ResponseEntity.notFound().build()); // Иначе возвращает код 404 Not Found
    }

    //localhost:8080/api/rooms/add
    @PostMapping("/add")
    public ResponseEntity<Room> createRoom(@RequestBody RoomDto roomDto) {
        Room createdRoom = roomService.createRoom(roomDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRoom); // Возвращает созданный номер с кодом 201 Created
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<Room> updateRoom(@PathVariable Long id, @RequestBody RoomDto roomDto) {
        try {
            Room updatedRoom = roomService.updateRoom(id, roomDto);
            return ResponseEntity.ok(updatedRoom); // Возвращает обновленный номер с кодом 200 OK
        } catch (RuntimeException ex) { // Обработка исключения, если номер не найден
            return ResponseEntity.notFound().build(); // Возвращает код 404 Not Found
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


    //http://localhost:8080/api/rooms/searchTitle?title=Люкс
    @GetMapping("/searchTitle")
    public ResponseEntity<List<Room>> searchRooms(@RequestParam(required = false) String title) {
        if (title != null && !title.isEmpty()) {
            return ResponseEntity.ok(roomService.searchByTitle(title));
        }
        return ResponseEntity.ok(roomService.getAllRooms());
    }

    //http://localhost:8080/api/rooms/sear?maxPrice=10000
    //http://localhost:8080/api/rooms/searchPrice?minPrice=9000&maxPrice=20000
    //http://localhost:8080/api/rooms/searchPrice?roomTitle=Стандарт&minPrice=5000&maxPrice=20000
    @GetMapping("/searchPrice")
    public ResponseEntity<List<Room>> searchRooms(
            @RequestParam(required = false) String roomTitle,
            @RequestParam(required = false, defaultValue = "0") double minPrice,
            @RequestParam(required = false, defaultValue = "100000") double maxPrice // Очень большая цена, по умолчанию возвращает все
    ) {
        return ResponseEntity.ok(roomService.searchRooms(roomTitle, minPrice, maxPrice));
    }
}


