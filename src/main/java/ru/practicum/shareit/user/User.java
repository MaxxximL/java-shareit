package ru.practicum.shareit.user;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    // Добавляем конструктор для создания объекта только с ID
    public User(Long id) {
        this.id = id;
    }
}