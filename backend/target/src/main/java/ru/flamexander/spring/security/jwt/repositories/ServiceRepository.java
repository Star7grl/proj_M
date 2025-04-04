package ru.flamexander.spring.security.jwt.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.flamexander.spring.security.jwt.entities.Services;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<Services, Long> {
    // Модифицируем существующий метод для поиска с частичным совпадением
    List<Services> findByServiceNameContainingIgnoreCase(String serviceName);
}
