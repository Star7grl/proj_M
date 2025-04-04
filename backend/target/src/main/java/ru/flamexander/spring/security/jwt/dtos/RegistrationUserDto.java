package ru.flamexander.spring.security.jwt.dtos;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class RegistrationUserDto {
    @NotBlank(message = "Логин обязателен")
    @Size(min = 4, max = 50, message = "Логин должен содержать от 4 до 50 символов")
    private String username;

    @NotBlank(message = "Пароль обязателен")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$",
            message = "Пароль должен содержать минимум 8 символов, одну заглавную букву, одну строчную букву, одну цифру и один специальный символ (@#$%^&+=!)"
    )
    private String password;

    @NotBlank
    private String confirmPassword;

    @NotBlank
    private String email;
}
