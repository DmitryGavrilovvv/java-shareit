package ru.practicum.shareit.user.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserDto {
    private Long id;
    @NotBlank
    private String name;

    @Email(message = "Поле должно содержать символ @")
    @NotBlank(message = "E-mail не может быть пустым")
    private String email;
}
