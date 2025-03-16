package ru.practicum.shareit.user.model;

import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Data;

@Builder(toBuilder = true)
@Data
public class UserUpdateDto {
    private String name;
    @Email(message = "Адрес электронной почты должен содержать символ '@'")
    private String email;
}
