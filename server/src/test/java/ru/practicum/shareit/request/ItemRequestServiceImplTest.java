package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.request.interfaces.ItemRequestService;
import ru.practicum.shareit.request.model.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequestDtoCreate;
import ru.practicum.shareit.user.interfaces.UserService;
import ru.practicum.shareit.user.model.UserDto;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemRequestServiceImplTest {

    @Autowired
    UserService userService;

    @Autowired
    ItemRequestService itemRequestService;

    static UserDto user1;
    static ItemDto item1;
    static ItemRequestDtoCreate itemRequest1;

    @BeforeAll
    static void beforeAll() {
        user1 = UserDto.builder().name("Yandex").email("yandex@practicum.ru").build();
        item1 = ItemDto.builder().name("Yandex").description("YandexPracticum").available(true).build();
        itemRequest1 = ItemRequestDtoCreate.builder().description("YandexPracticum").build();
    }

    @Test
    void shouldCreateAndGetItemRequest() {
        UserDto user = userService.createUser(user1);
        ItemRequestDto itemRequest = itemRequestService.createItemRequest(user.getId(), itemRequest1);
        ItemRequestDto getItemRequest = itemRequestService.getItemRequestById(itemRequest.getId());

        assertThat(itemRequest.getId()).isEqualTo(getItemRequest.getId());
        assertThat(itemRequest.getDescription()).isEqualTo(getItemRequest.getDescription());
        assertThat(itemRequest.getRequestorId()).isEqualTo(getItemRequest.getRequestorId());
    }

    @Test
    void shouldThrowExceptionWhenUserIsNotFound() {
        assertThatThrownBy(() -> itemRequestService.createItemRequest(100L, itemRequest1))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldThrowExceptionWhenIdIsNull() {
        assertThatThrownBy(() -> itemRequestService.createItemRequest(null, itemRequest1))
                .isInstanceOf(InvalidDataAccessApiUsageException.class);
    }

    @Test
    void shouldGetItemRequestByRequestorId() {
        UserDto user = userService.createUser(user1);
        ItemRequestDto itemRequest = itemRequestService.createItemRequest(user.getId(), itemRequest1);

        List<ItemRequestDto> itemRequests = itemRequestService.getAllItemRequestsByRequestor(user.getId()).stream()
                .toList();

        assertThat(itemRequests).hasSize(1);
        assertThat(itemRequests.getFirst().getId()).isEqualTo(itemRequest.getId());
        assertThat(itemRequests.getFirst().getDescription()).isEqualTo(itemRequest.getDescription());
        assertThat(itemRequests.getFirst().getRequestorId()).isEqualTo(itemRequest.getRequestorId());
    }

    @Test
    void shouldGetAllItemRequests() {
        UserDto user = userService.createUser(user1);
        ItemRequestDto itemRequest = itemRequestService.createItemRequest(user.getId(), itemRequest1);

        List<ItemRequestDto> itemRequests = itemRequestService.getAllItemRequests().stream().toList();

        assertThat(itemRequests).hasSize(1);
        assertThat(itemRequests.getFirst().getId()).isEqualTo(itemRequest.getId());
        assertThat(itemRequests.getFirst().getDescription()).isEqualTo(itemRequest.getDescription());
        assertThat(itemRequests.getFirst().getRequestorId()).isEqualTo(itemRequest.getRequestorId());
    }
}
