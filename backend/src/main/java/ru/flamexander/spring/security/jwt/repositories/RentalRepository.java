// RentalRepository.java
package ru.flamexander.spring.security.jwt.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.flamexander.spring.security.jwt.entities.Rental;


@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {

}