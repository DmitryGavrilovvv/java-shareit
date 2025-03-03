package ru.practicum.shareit.user.model;

import jakarta.persistence.*;
import lombok.*;

@Builder(toBuilder = true)
@Getter
@Setter
@ToString
@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
    private String name;
    @Column(length = 512, nullable = false, unique = true)
    private String email;
}
