package ru.flamexander.spring.security.jwt.entities;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "rentals")
public class Rental {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "visitor_first_name")
    private String visitorFirstName;

    @Column(name = "visitor_last_name")
    private String visitorLastName;

    @Column(name = "visitor_phone")
    private String visitorPhone;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    @Column(name = "check_in_date")
    private LocalDate checkInDate;

    @Column(name = "check_out_date")
    private LocalDate checkOutDate;
}