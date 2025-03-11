package ru.practicum.shareit.item.interfaces;

import ru.practicum.shareit.item.model.*;

import java.util.Collection;

public interface ItemService {
    ItemDtoRequest createItem(Long userId, ItemDto dto);

    ItemDto updateItem(Long itemId, UpdateItemDto dto, Long userId);

    ExtendedItemDto getItemById(Long itemId);

    Collection<ItemDto> getAllItemsByOwner(Long id);

    Collection<ItemDto> searchItems(String text);

    CommentDto addCommentToItem(long authorId, long itemId, CommentDto comment);
}
