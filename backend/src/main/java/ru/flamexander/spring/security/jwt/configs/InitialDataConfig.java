package ru.flamexander.spring.security.jwt.configs;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.flamexander.spring.security.jwt.entities.Role;
import ru.flamexander.spring.security.jwt.entities.User;
import ru.flamexander.spring.security.jwt.repositories.RoleRepository;
import ru.flamexander.spring.security.jwt.repositories.UserRepository;

@Configuration
public class InitialDataConfig {

    @Bean
    public CommandLineRunner initializeData(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Проверка и создание роли ROLE_USER
            if (!roleRepository.findByName("ROLE_USER").isPresent()) {
                Role userRole = new Role();
                userRole.setName("ROLE_USER");
                roleRepository.save(userRole);
            }

            // Проверка и создание роли ROLE_HOSTES
            if (!roleRepository.findByName("ROLE_HOSTES").isPresent()) {
                Role hostesRole = new Role();
                hostesRole.setName("ROLE_HOSTES");
                roleRepository.save(hostesRole);
            }

            // Проверка и создание пользователя hostes
            if (!userRepository.findByUsername("hostes").isPresent()) {
                User hostesUser = new User();
                hostesUser.setUsername("hostes");
                hostesUser.setPassword("$2a$10$eaHeba7UuOQ2sSoNaTUeze974DVeFqhriIhH2iJqQmZbPp/Iuk.9a"); // Захешированный пароль
                hostesUser.setEmail("hostes@example.com"); // Пример email
                hostesUser.setRole(roleRepository.findByName("ROLE_HOSTES").get());
                userRepository.save(hostesUser);
            }
        };
    }
}