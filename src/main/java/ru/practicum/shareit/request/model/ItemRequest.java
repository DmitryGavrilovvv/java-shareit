package ru.practicum.shareit.request.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Builder
@Data
public class ItemRequest {
    private final Long id;
    private String description;
    private final User requestor;
    private final LocalDateTime created;
}
