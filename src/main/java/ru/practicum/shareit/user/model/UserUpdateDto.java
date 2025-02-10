package ru.practicum.shareit.user.model;

import lombok.Data;

@Data
public class UserUpdateDto {
    private Long id;
    private String name;
    private String email;
}
