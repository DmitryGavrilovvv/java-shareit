package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createItem(@Valid @RequestBody ItemDto dto,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрос на добавление новой вещи: {} пользователю с id = {}", dto.getName(), userId);
        return itemClient.createItem(userId, dto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@Valid @PathVariable Long itemId,
                                             @RequestBody ItemDto dto,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрос на обновление вещи с id = {} у пользователя с id = {}", itemId, userId);
        return itemClient.updateItem(itemId, dto, userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable Long itemId,
                                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрос на получение вещи с id = {} у пользователя с id = {}", itemId, userId);
        return itemClient.getItemById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsByOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрос на получение списка всех вещей пользователя с id = {}", userId);
        return itemClient.getAllItemsByOwner(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text,
                                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрос на поиск вещей пользователя с id = {} с текстом '{}'", userId, text);
        return itemClient.searchItems(text, userId);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createComment(@Valid @RequestBody CommentDto dto,
                                                @RequestHeader("X-Sharer-User-Id") Long userId,
                                                @PathVariable Long itemId) {
        log.info("Запрос на добавление комментария к вещи с id = {} пользователем с id = {}", itemId, userId);
        return itemClient.createComment(itemId, userId, dto);
    }
}
