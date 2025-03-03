package ru.practicum.shareit.item.interfaces;

import ru.practicum.shareit.item.model.CommentDto;
import ru.practicum.shareit.item.model.ExtendedItemDto;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.item.model.UpdateItemDto;

import java.util.Collection;

public interface ItemService {
    ItemDto createItem(Long userId, ItemDto dto);

    ItemDto updateItem(Long itemId, UpdateItemDto dto, Long userId);

    ExtendedItemDto getItemById(Long itemId);

    Collection<ItemDto> getAllItemsByOwner(Long id);

    Collection<ItemDto> searchItems(String text);

    CommentDto addCommentToItem(long authorId, long itemId, CommentDto comment);
}
