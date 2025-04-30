package ru.flamexander.spring.security.jwt.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.flamexander.spring.security.jwt.dtos.UserDto;
import ru.flamexander.spring.security.jwt.entities.User;
import ru.flamexander.spring.security.jwt.service.UserService;

@CrossOrigin(origins = "http://localhost:5174")
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.findById(id)
                .map(user -> ResponseEntity.ok(user))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        User updatedUser = userService.updateUser(id, userDetails);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long id) {
        boolean isDeleted = userService.deleteById(id);
        if (isDeleted) {
            return ResponseEntity.noContent().build(); // Возвращаем 204 No Content, если удаление прошло успешно
        } else {
            return ResponseEntity.notFound().build(); // Возвращаем 404 Not Found, если пользователь не найден
        }
    }

    @PutMapping("/profile/{id}")
    public ResponseEntity<User> updateUserProfile(@PathVariable Long id, @RequestBody UserDto userDto) {
        // Получаем текущего аутентифицированного пользователя
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) auth.getPrincipal();

        // Извлекаем пользователя из Optional
        User currentUser = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        Long currentUserId = currentUser.getId();

        // Проверяем, совпадает ли ID из запроса с ID текущего пользователя
        if (!currentUserId.equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null); // 403, если не совпадает
        }

        // Обновляем профиль
        User updatedUser = userService.updateUserProfile(id, userDto.getFirstName(), userDto.getLastName());
        if (updatedUser != null) {
            return ResponseEntity.ok(updatedUser);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

