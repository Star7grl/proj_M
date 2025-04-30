package ru.flamexander.spring.security.jwt.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SequenceGenerator(name = "default_generator", sequenceName = "rooms_sequence", allocationSize = 1)
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "default_generator")
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

    // Изменено: добавлена аннотация columnDefinition = "TEXT" для поддержки длинных URL
    // @Column(name = "image_url", columnDefinition = "TEXT")
    // private String imageUrl;
    // Мой комментарий: Поле imageUrl удалено, так как теперь изображения хранятся в отдельной сущности RoomImage.

    // Мой комментарий: Добавлена связь OneToMany с RoomImage для поддержки нескольких изображений.
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoomImage> images = new ArrayList<>();
}