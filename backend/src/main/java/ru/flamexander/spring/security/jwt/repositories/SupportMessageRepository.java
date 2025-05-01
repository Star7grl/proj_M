package ru.flamexander.spring.security.jwt.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.flamexander.spring.security.jwt.entities.SupportMessage;

@Repository
public interface SupportMessageRepository extends JpaRepository<SupportMessage, Long> {
}