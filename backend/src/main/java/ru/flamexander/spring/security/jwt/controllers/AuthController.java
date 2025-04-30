package ru.flamexander.spring.security.jwt.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.FieldError; // Новый импорт
import ru.flamexander.spring.security.jwt.configs.CustomUserDetails;
import ru.flamexander.spring.security.jwt.dtos.JwtRequest;
import ru.flamexander.spring.security.jwt.dtos.JwtResponse;
import ru.flamexander.spring.security.jwt.dtos.RegistrationUserDto;
import ru.flamexander.spring.security.jwt.dtos.UserDto;
import ru.flamexander.spring.security.jwt.entities.User;
import ru.flamexander.spring.security.jwt.service.RegAuto.AuthService;
import ru.flamexander.spring.security.jwt.utils.JwtTokenUtils;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5174", allowCredentials = "true")
public class AuthController {
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtils jwtTokenUtils;

    @Autowired
    public AuthController(AuthService authService, AuthenticationManager authenticationManager, JwtTokenUtils jwtTokenUtils) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtils = jwtTokenUtils;
    }

    // Регистрация нового пользователя
    @PostMapping("/reg")
    public ResponseEntity<?> createNewUser(@Valid @RequestBody RegistrationUserDto registrationUserDto, BindingResult bindingResult) {
        log.info("Пришел запрос на регистрацию");

        if (bindingResult.hasErrors()) {
            log.info("Ошибка валидации данных");
            List<String> errors = bindingResult.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errors);
        }

        log.info("Данные валидированы успешно");
        return authService.createNewUser(registrationUserDto);
    }

    // Проверка аутентификации
    @GetMapping("/check")
    public ResponseEntity<?> checkAuth() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(401).build();
    }

    // Вход пользователя
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody JwtRequest authRequest, HttpServletResponse response) {
        log.info("Пришел запрос на вход");

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
            log.info("Аутентификация прошла успешно");

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = jwtTokenUtils.generateToken((UserDetails) authentication.getPrincipal());
            log.info("Токен сгенерирован успешно");

            ResponseCookie cookie = ResponseCookie.from("JWT", jwt)
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(3600)
                    .sameSite("None")
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
            log.info("Secure cookie с токеном добавлен в заголовки ответа");

            return ResponseEntity.ok()
                    .header("Content-Type", "application/json")
                    .body(new JwtResponse(jwt));

        } catch (AuthenticationException e) {
            log.error("Ошибка аутентификации", e);
            return ResponseEntity.badRequest().body("Неправильный логин или пароль");
        }
    }

    // Выход пользователя
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("JWT", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("None")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok("Выход выполнен успешно");
    }

    // Получение информации о текущем пользователе (занят для профиля)
    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
            User user = userDetails.getUser();
            // Новое: добавляем роль в UserDto и photoPath
            UserDto userDto = new UserDto(user.getId(), user.getUsername(), user.getEmail(), user.getRole().getName(), user.getFirstName(), user.getLastName(), user.getPhotoPath());
            return ResponseEntity.ok(userDto);
        }
        // Если пользователь не аутентифицирован, возвращаем 401 Unauthorized
        return ResponseEntity.status(401).build();
    }

}



// токен в куки
//    @PostMapping("/auth")
//    public ResponseEntity<?> createAuthToken(@RequestBody JwtRequest authRequest, HttpServletResponse response) {
//        ResponseEntity<?> responseEntity = authService.createAuthToken(authRequest); // Получаем ResponseEntity от сервиса, обработка запроса
//
//        if (responseEntity.getStatusCode().is2xxSuccessful()) {
//            // Если токен успешно создан, добавляем его в cookie
//            String token = ((JwtResponse) responseEntity.getBody()).getToken(); // Извлекаем токен из ответа
//            Cookie cookie = new Cookie("JWT", token);
//            cookie.setHttpOnly(true); // Защита от XSS атак
//            cookie.setSecure(true); // Использовать только через HTTPS (если у вас настроен HTTPS)
//            cookie.setPath("/"); // Доступно для всех путей
//            cookie.setMaxAge(3600); // Время жизни 1 час
//
//            response.addCookie(cookie); // Добавляем cookie в ответ
//        }
//
//        return responseEntity; // Возвращаем ответ от сервиса
//    }



// Было изначально
//    @PostMapping("/auth") //Эта аннотация указывает, что метод `createAuthToken` будет обрабатывать POST-запросы, адресованные к `/auth`
//    public ResponseEntity<?> createAuthToken(@RequestBody JwtRequest authRequest, HttpServletResponse response) {
//        String token = authService.createAuthToken(authRequest); // Получаем токен от сервиса
//
//        // Создаем cookie для хранения JWT токена
//        Cookie cookie = new Cookie("JWT", token);
//        cookie.setHttpOnly(true); // Защита от XSS атак
//        cookie.setSecure(true); // Использовать только через HTTPS (если у вас настроен HTTPS)
//        cookie.setPath("/"); // Доступно для всех путей
//        cookie.setMaxAge(3600); // Время жизни 1 час
//
//        response.addCookie(cookie); // Добавляем cookie в ответ
//
//        return ResponseEntity.ok("Токен установлен в cookie");
//    }
//    public ResponseEntity<?> createAuthToken(@RequestBody JwtRequest authRequest) {
//        return authService.createAuthToken(authRequest);
//    }



