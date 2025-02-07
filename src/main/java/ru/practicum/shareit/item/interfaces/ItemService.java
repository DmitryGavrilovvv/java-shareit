package ru.practicum.shareit.item.interfaces;

import ru.practicum.shareit.item.model.ItemDto;

import java.util.Collection;

public interface ItemService {
    ItemDto createItem(Long userId, ItemDto dto);

    ItemDto updateItem(Long itemId, ItemDto dto, Long userId);

    ItemDto getItemById(Long itemId);

    Collection<ItemDto> getAllItemsByOwner(Long id);

    Collection<ItemDto> searchItems(String text);
}
