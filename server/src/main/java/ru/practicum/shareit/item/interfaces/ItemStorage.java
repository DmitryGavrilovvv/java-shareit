package ru.practicum.shareit.item.interfaces;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemStorage {
    Item createItem(Item item);

    Item updateItem(Item newItem);

    Item getItemById(Long id);

    Collection<Item> getAllItemsByOwner(Long userId);

    Collection<Item> searchItems(String text);
}
