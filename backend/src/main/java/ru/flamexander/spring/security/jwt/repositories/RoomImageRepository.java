package ru.flamexander.spring.security.jwt.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.flamexander.spring.security.jwt.entities.RoomImage;

@Repository
public interface RoomImageRepository extends JpaRepository<RoomImage, Long> {

}