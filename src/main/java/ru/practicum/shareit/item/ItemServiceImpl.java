package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ShareItException;
import ru.practicum.shareit.exception.ShareItExceptionCodes;
import ru.practicum.shareit.item.interfaces.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto createItem(Long userId, ItemDto dto) {
        userRepository.getUserById(userId);
        log.debug("Добавление новой вещи с именем: {} пользователю с id = {}", dto.getName(), userId);
        Item item = ItemMapper.mapToItem(dto);
        item.setOwner(userRepository.getUserById(userId));
        return ItemMapper.mapToItemDto(itemRepository.createItem(item));
    }

    @Override
    public ItemDto updateItem(Long itemId, ItemDto dto, Long userId) {
        checkId(itemId);
        Item item = itemRepository.getItemById(itemId);
        if (!item.getOwner().getId().equals(userId)) {
            log.error("Пользователь с id = {} не владелец вещи с id = {}", userId, itemId);
            throw new ShareItException(ShareItExceptionCodes.NOT_OWNER_UPDATE);
        }
        log.debug("Обновление вещи с id = {} пользователя с id = {}", itemId, userId);
        if (dto.getName() != null && !dto.getName().isBlank()) {
            item.setName(dto.getName());
        }
        if (dto.getDescription() != null && !dto.getDescription().isBlank()) {
            item.setDescription(dto.getDescription());
        }
        if (dto.getAvailable() != null) {
            item.setAvailable(dto.getAvailable());
        }
        return ItemMapper.mapToItemDto(itemRepository.updateItem(item));
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        checkId(itemId);
        return ItemMapper.mapToItemDto(itemRepository.getItemById(itemId));
    }

    @Override
    public Collection<ItemDto> getAllItemsByOwner(Long id) {
        log.debug("Получение списка всех вещей пользовтеля с id = {}", id);
        return itemRepository.getAllItemsByOwner(id).stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    @Override
    public Collection<ItemDto> searchItems(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        log.debug("Получение списка доступных вещей с текстом '{}'", text);
        return itemRepository.searchItems(text).stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    private void checkId(Long id) {
        if (id == null) {
            log.error("id вещи не указан");
            throw new ShareItException(ShareItExceptionCodes.EMPTY_ITEM_ID);
        }
    }
}
