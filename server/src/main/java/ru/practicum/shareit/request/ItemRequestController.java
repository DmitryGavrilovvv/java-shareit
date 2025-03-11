package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.interfaces.ItemRequestService;
import ru.practicum.shareit.request.model.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequestDtoCreate;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDto createItemRequest(@RequestBody ItemRequestDtoCreate dto,
                                            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрос на добавление нового запроса вещи: '{}' пользователя с id = {}", dto.getDescription(), userId);
        return itemRequestService.createItemRequest(userId, dto);
    }

    @GetMapping
    public Collection<ItemRequestDto> getAllItemRequestsByRequestor(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрос на получение списка всех запросов пользователя с id = {}", userId);
        return itemRequestService.getAllItemRequestsByRequestor(userId);
    }

    @GetMapping("/all")
    public Collection<ItemRequestDto> getAllItemRequests() {
        log.info("Запрос на получение списка всех запросов, созданных другими пользователями");
        return itemRequestService.getAllItemRequests();
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequestById(@PathVariable Long requestId) {
        log.info("Запрос на получение запроса с id = {}", requestId);
        return itemRequestService.getItemRequestById(requestId);
    }
}
