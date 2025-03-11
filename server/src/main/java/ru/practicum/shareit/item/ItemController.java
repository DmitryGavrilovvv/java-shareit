package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.interfaces.ItemService;
import ru.practicum.shareit.item.model.*;

import java.util.Collection;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private static final String HEADER_SHARER_USER_ID = "X-Sharer-User-Id";
    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDtoRequest createItem(@Valid @RequestBody CreateItemDto dto, @RequestHeader(HEADER_SHARER_USER_ID) Long userId) {
        log.info("Запрос на добавление новой вещи: {} пользователю с id = {}", dto.getName(), userId);
        return itemService.createItem(userId, dto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable Long itemId, @RequestBody UpdateItemDto dto,
                              @RequestHeader(HEADER_SHARER_USER_ID) Long userId) {
        log.info("Запрос на обновление вещи с id = {} у пользователя с id = {}", itemId, userId);
        return itemService.updateItem(itemId, dto, userId);
    }

    @GetMapping("/{itemId}")
    public ExtendedItemDto getItemById(@PathVariable Long itemId, @RequestHeader(HEADER_SHARER_USER_ID) Long userId) {
        log.info("Запрос на получение вещи с id = {} у пользователя с id = {}", itemId, userId);
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public Collection<ItemDto> getAllItemsByOwner(@RequestHeader(HEADER_SHARER_USER_ID) Long userId) {
        log.info("Запрос на получение списка всех вещей пользователя с id = {}", userId);
        return itemService.getAllItemsByOwner(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItems(@RequestParam String text, @RequestHeader(HEADER_SHARER_USER_ID) Long userId) {
        log.info("Запрос на поиск вещей пользователя с id = {} с текстом '{}'", userId, text);
        return itemService.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addCommentToItem(@PathVariable long itemId, @RequestHeader(name = "X-Sharer-User-Id") long authorId, @RequestBody @Valid CommentDto dto) {
        return itemService.addCommentToItem(authorId, itemId, dto);
    }
}
