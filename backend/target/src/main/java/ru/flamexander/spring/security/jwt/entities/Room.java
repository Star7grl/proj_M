package ru.flamexander.spring.security.jwt.entities;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "rooms")
@Getter
@Setter
@NoArgsConstructor // Пустой конструктор
@AllArgsConstructor // Конструктор со всеми параметрами
@SequenceGenerator(name = "default_generator", sequenceName = "rooms_sequence", allocationSize = 1)
public class Room {

    @Id // Указываем, что это поле является идентификатором
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "default_generator") // Генерация значения через последовательность
    @Column(name = "room_id")
    private Long roomId;

    @Column(name = "room_title")
    private String roomTitle;

    @Column(name = "room_type")
    private String roomType;

    @Column(name = "description")
    private String description;

    @Column(name = "price")
    private Double price;

    @Column(name = "status")
    private String status;
}
