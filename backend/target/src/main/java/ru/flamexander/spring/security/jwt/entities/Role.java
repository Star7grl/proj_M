package ru.flamexander.spring.security.jwt.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

@Entity
@Data
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    // Если нужна двусторонняя связь
    @JsonIgnore
    @OneToMany(mappedBy = "role", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<User> users = new ArrayList<>();
}
