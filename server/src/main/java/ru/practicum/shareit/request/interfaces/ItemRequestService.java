package ru.practicum.shareit.request.interfaces;

import ru.practicum.shareit.request.model.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequestDtoCreate;

import java.util.Collection;

public interface ItemRequestService {
    ItemRequestDto createItemRequest(Long userId, ItemRequestDtoCreate dto);

    Collection<ItemRequestDto> getAllItemRequestsByRequestor(Long userId);

    Collection<ItemRequestDto> getAllItemRequests();

    ItemRequestDto getItemRequestById(Long requestId);
}
