package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.ShareItException;
import ru.practicum.shareit.exception.ShareItExceptionCodes;
import ru.practicum.shareit.item.interfaces.ItemService;
import ru.practicum.shareit.item.model.*;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto createItem(Long userId, ItemDto dto) {
        userRepository.findById(userId);
        log.debug("Добавление новой вещи с именем: {} пользователю с id = {}", dto.getName(), userId);
        Item item = ItemMapper.mapToItem(dto);
        item.setOwner(userRepository.findById(userId).orElseThrow(() -> new ShareItException(ShareItExceptionCodes.USER_NOT_FOUND, userId)));
        return ItemMapper.mapToItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto updateItem(Long itemId, UpdateItemDto dto, Long userId) {
        checkId(itemId);
        Item item = itemRepository.findById(itemId).orElseThrow();
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
        return ItemMapper.mapToItemDto(itemRepository.save(item));
    }

    @Override
    public ExtendedItemDto getItemById(Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ShareItException(ShareItExceptionCodes.ITEM_NOT_FOUND, itemId));

        Collection<Booking> bookings = bookingRepository.findByItemId(itemId);

        return ItemMapper.mapToExtendedItemDto(item, bookings);
    }

    @Override
    public Collection<ItemDto> getAllItemsByOwner(Long id) {
        log.debug("Получение списка всех вещей пользовтеля с id = {}", id);
        return itemRepository.findByOwnerId(id).stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    @Override
    public Collection<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        return itemRepository.findByNameOrDescriptionContainingIgnoreCase(text).stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    @Override
    public CommentDto addCommentToItem(long authorId, long itemId, CommentDto commentDto) {
        Comment comment = CommentMapper.mapToComment(authorId, itemId, commentDto);
        Collection<Booking> authorBookings = bookingRepository.findByBookerIdAndItemId(comment.getAuthor().getId(), comment.getItem().getId());

        if (authorBookings.isEmpty() || authorBookings.stream()
                .filter(b -> b.getEnd().isBefore(LocalDateTime.now()))
                .filter(booking -> booking.getItem().getId() == itemId)
                .toList().isEmpty()) {
            throw new ShareItException(ShareItExceptionCodes.COMMIT_DENIED, comment.getAuthor().getId(), comment.getItem().getId());
        }

        comment.setItem(itemRepository.findById(comment.getItem().getId()).orElseThrow(() -> new ShareItException(ShareItExceptionCodes.ITEM_NOT_FOUND, comment.getItem().getId())));
        comment.setAuthor(userRepository.findById(comment.getAuthor().getId()).orElseThrow(() -> new ShareItException(ShareItExceptionCodes.USER_NOT_FOUND, comment.getAuthor().getId())));

        return CommentMapper.mapToCommentDto(commentRepository.save(comment));
    }

    private void checkId(Long id) {
        if (id == null) {
            log.error("id вещи не указан");
            throw new ShareItException(ShareItExceptionCodes.EMPTY_ITEM_ID);
        }
    }
}
