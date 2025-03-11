package ru.practicum.shareit.request;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDtoRequest;
import ru.practicum.shareit.request.interfaces.ItemRequestService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequestDtoCreate;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ItemRequestDto createItemRequest(Long userId, ItemRequestDtoCreate dto) {
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(dto, userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %s не найден", userId))));
        itemRequest.setCreated(LocalDateTime.now());
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public Collection<ItemRequestDto> getAllItemRequestsByRequestor(Long userId) {
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(userId);
        List<Item> items = itemRepository.findAllByItemRequestIdIn(itemRequests.stream()
                .map(ItemRequest::getId).toList());
        List<ItemDtoRequest> answers = items.stream().map(ItemMapper::toItemDtoRequest).toList();

        Map<Long, List<ItemDtoRequest>> groupedAnswers = answers.stream()
                .collect(Collectors.groupingBy(ItemDtoRequest::getRequestId));

        return itemRequests.stream()
                .map(itemRequest -> ItemRequestMapper.toItemRequestDto(itemRequest,
                        groupedAnswers.getOrDefault(itemRequest.getId(), Collections.emptyList()))).toList();
    }

    @Override
    public Collection<ItemRequestDto> getAllItemRequests() {
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        return itemRequestRepository.findAll(sort).stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .toList();
    }

    @Override
    public ItemRequestDto getItemRequestById(Long requestId) {
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с id = " + requestId + " не найден"));
        List<ItemDtoRequest> answers = itemRepository.findAllByItemRequestIdIn(List.of(requestId)).stream()
                .map(ItemMapper::toItemDtoRequest).toList();
        return ItemRequestMapper.toItemRequestDto(itemRequest, answers);
    }

    private User getUserById(Long id) {
        checkId(id);
        log.debug("Получение пользователя с id = {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + id + " не найден"));
    }

    private void checkId(Long id) {
        if (id == null) {
            log.error("id не указан");
            throw new ValidationException("id должен быть указан");
        }
    }
}
