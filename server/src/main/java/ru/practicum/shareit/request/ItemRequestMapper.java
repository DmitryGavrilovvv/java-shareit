package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.ItemDtoRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequestDtoCreate;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        if (itemRequest == null) {
            return null;
        }
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requestorId(itemRequest.getRequestor().getId())
                .created(itemRequest.getCreated())
                .build();
    }

    public static ItemRequest toItemRequest(ItemRequestDtoCreate dto, User requestor) {
        return ItemRequest.builder()
                .description(dto.getDescription())
                .requestor(requestor)
                .build();
    }

    public static ItemRequest toItemRequest(ItemRequestDto dto, User requestor) {
        if (dto == null) {
            return null;
        }
        return ItemRequest.builder()
                .description(dto.getDescription())
                .requestor(requestor)
                .build();
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest, List<ItemDtoRequest> items) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requestorId(itemRequest.getRequestor().getId())
                .created(itemRequest.getCreated())
                .items(items)
                .build();
    }
}
