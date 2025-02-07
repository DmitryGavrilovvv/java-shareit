package ru.practicum.shareit.request.model;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
@Builder
public class ItemRequestDto {
    private final Long id;
    private String description;
    @NotNull
    private final User requestor;
    private final LocalDateTime created;
}
