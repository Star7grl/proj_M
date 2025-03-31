package ru.flamexander.spring.security.jwt;

import java.util.regex.Pattern;

public class ValidationUtils {

    // Регулярное выражение для проверки пароля
    private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>]).{8,}$";
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);

    // Регулярное выражение для проверки email
    private static final String EMAIL_REGEX = "^[\\w.-]+@[\\w.-]+\\.(com|ru)$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    public static boolean validatePassword(String password) {
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    public static boolean validateEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public static void main(String[] args) {
        // Примеры проверки пароля
        System.out.println("Password validation:");
        System.out.println(validatePassword("Password1!")); // true
        System.out.println(validatePassword("pass!")); // false
        System.out.println(validatePassword("PASSWORD1!")); // false
        System.out.println(validatePassword("Password!")); // false
        System.out.println(validatePassword("Passw0r!")); // true

        // Примеры проверки email
        System.out.println("\nEmail validation:");
        System.out.println(validateEmail("example@gmail.com")); // true
        System.out.println(validateEmail("example@mail.ru")); // true
        System.out.println(validateEmail("example@invalid")); // false
        System.out.println(validateEmail("example@domain.com.ru")); // false
    }
}
