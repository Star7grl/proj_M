package ru.flamexander.spring.security.jwt.entities;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Rental {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String visitorFirstName;
    private String visitorLastName;
    private String visitorPhone;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToMany
    @JoinTable(
            name = "rental_services",
            joinColumns = @JoinColumn(name = "rental_id"),
            inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private List<Services> services = new ArrayList<>();

    private LocalDate checkInDate;
    private LocalDate checkOutDate;

    @Column(name = "hostes_id")
    private Long hostesId;

    @Transient
    private List<Long> serviceIds; // Временное поле для получения ID услуг из запроса
}