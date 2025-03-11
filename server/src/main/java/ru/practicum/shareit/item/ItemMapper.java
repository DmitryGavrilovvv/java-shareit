package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.*;
import ru.practicum.shareit.request.ItemRequestMapper;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {
    public static Item mapToItem(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(itemDto.getOwner())
                .itemRequest(ItemRequestMapper.toItemRequest(itemDto.getRequest(), itemDto.getOwner()))
                .build();
    }

    public static Item mapToItem(CreateItemDto dto) {
        return Item.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .available(dto.getAvailable())
                .build();
    }

    public static ItemDto mapToItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(item.getOwner())
                .comments(item.getComments())
                .build();
    }

    public static ExtendedItemDto mapToExtendedItemDto(Item item, Collection<Booking> itemBookings) {
        ExtendedItemDto.ExtendedItemDtoBuilder builder = ExtendedItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .comments(item.getComments())
                .owner(item.getOwner())
                .itemRequest(ItemRequestMapper.toItemRequestDto(item.getItemRequest()));

        itemBookings.stream()
                .filter(b -> b.getStart().isBefore(LocalDateTime.now()) && b.getEnd().isAfter(LocalDateTime.now()))
                .findFirst()
                .ifPresent(booking -> builder.lastBooking(booking.getEnd()));

        itemBookings.stream()
                .filter(b -> b.getStart().isAfter(LocalDateTime.now()))
                .min(Comparator.comparing(Booking::getStart))
                .ifPresent(booking -> builder.nextBooking(booking.getStart()));

        return builder.build();
    }

    public static ItemDtoRequest toItemDtoRequest(Item item) {
        return ItemDtoRequest.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .ownerId(item.getOwner().getId())
                .requestId(item.getItemRequest().getId())
                .build();
    }

    public static ItemDtoRequest toItemDtoRequest(Item item, Long itemRequestId) {
        return ItemDtoRequest.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .ownerId(item.getOwner().getId())
                .requestId(itemRequestId)
                .build();
    }
}
