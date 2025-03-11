package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.model.ItemRequestDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;

@Builder(toBuilder = true)
@Data
public final class ExtendedItemDto {
    private final long id;

    private final String name;

    private final String description;

    private final Boolean available;

    private final User owner;

    private Collection<String> comments;

    private final LocalDateTime lastBooking;

    private final LocalDateTime nextBooking;


    ItemRequestDto itemRequest;
}
