package ru.flamexander.spring.security.jwt.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String role;
    private String firstName;
    private String lastName;
    private String photoPath; // Добавляем поле photoPath

    public UserDto(Long id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = null;
        this.firstName = null;
        this.lastName = null;
        this.photoPath = null;
    }

    public UserDto(Long id, String username, String email, String role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.firstName = null;
        this.lastName = null;
        this.photoPath = null;
    }

}
