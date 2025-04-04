package ru.flamexander.spring.security.jwt.configs;


import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.flamexander.spring.security.jwt.entities.Role;
import ru.flamexander.spring.security.jwt.repositories.RoleRepository;

@Configuration
public class InitialDataConfig {

    @Bean
    public CommandLineRunner initializeData(RoleRepository roleRepository) {
        return args -> {
            if (!roleRepository.findByName("ROLE_USER").isPresent()) {
                Role userRole = new Role();
                userRole.setName("ROLE_USER");
                roleRepository.save(userRole);
            }
        };
    }
}
