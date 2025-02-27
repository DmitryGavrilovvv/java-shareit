package ru.practicum.shareit.item.model;

import lombok.Data;

@Data
public final class UpdateItemDto {
    private final String name;
    private final String description;
    private final Boolean available;
}
