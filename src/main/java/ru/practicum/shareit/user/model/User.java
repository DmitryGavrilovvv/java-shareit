package ru.practicum.shareit.user.model;

import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class User {
    private Long id;
    private String name;
    @Email(message = "Поле должно содержать знак @")
    private String email;
}
