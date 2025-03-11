package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.model.ItemDtoRequest;
import ru.practicum.shareit.request.interfaces.ItemRequestService;
import ru.practicum.shareit.request.model.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequestDtoCreate;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemRequestControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ItemRequestService itemRequestService;

    ItemRequestDtoCreate itemRequest1;

    @BeforeEach
    void beforeEach() {
        itemRequest1 = ItemRequestDtoCreate.builder().description("YandexPracticum").build();
    }

    @Test
    void shouldReturnCreatedWhenCreateItemRequest() throws Exception {
        ItemRequestDto itemRequest2 = ItemRequestDto.builder()
                .id(1L)
                .description("YandexPracticum")
                .requestorId(1L)
                .created(LocalDateTime.now())
                .build();

        when(itemRequestService.createItemRequest(1L, itemRequest1)).thenReturn(itemRequest2);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequest1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("YandexPracticum"))
                .andExpect(jsonPath("$.requestorId").value(1));
    }

    @Test
    void shouldReturnOkWhenGetItemRequestsByRequestor() throws Exception {
        ItemDtoRequest item = ItemDtoRequest.builder()
                .id(1L)
                .name("Yandex")
                .description("YandexPracticum")
                .available(true)
                .ownerId(1L)
                .build();
        ItemRequestDto itemRequest2 = ItemRequestDto.builder()
                .id(1L)
                .description("YandexPracticum")
                .requestorId(1L)
                .created(LocalDateTime.now())
                .items(List.of(item))
                .build();
        List<ItemRequestDto> requests = List.of(itemRequest2);

        when(itemRequestService.getAllItemRequestsByRequestor(1L)).thenReturn(requests);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("YandexPracticum"))
                .andExpect(jsonPath("$[0].requestorId").value(1))
                .andExpect(jsonPath("$[0].items[0].id").value(1))
                .andExpect(jsonPath("$[0].items[0].name").value("Yandex"))
                .andExpect(jsonPath("$[0].items[0].description").value("YandexPracticum"))
                .andExpect(jsonPath("$[0].items[0].available").value(true))
                .andExpect(jsonPath("$[0].items[0].ownerId").value(1));
    }

    @Test
    void shouldReturnOkWhenGetItemRequests() throws Exception {
        ItemRequestDto itemRequest2 = ItemRequestDto.builder()
                .id(1L)
                .description("YandexPracticum")
                .requestorId(1L)
                .created(LocalDateTime.now())
                .build();

        List<ItemRequestDto> requests = List.of(itemRequest2);

        when(itemRequestService.getAllItemRequests()).thenReturn(requests);

        mockMvc.perform(get("/requests/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("YandexPracticum"))
                .andExpect(jsonPath("$[0].requestorId").value(1));
    }

    @Test
    void shouldReturnOkWhenGetItemRequestById() throws Exception {
        ItemDtoRequest item = ItemDtoRequest.builder()
                .id(1L)
                .name("Yandex")
                .description("YandexPracticum")
                .available(true)
                .ownerId(1L)
                .build();
        ItemRequestDto itemRequest2 = ItemRequestDto.builder()
                .id(1L)
                .description("YandexPracticum")
                .requestorId(1L)
                .created(LocalDateTime.now())
                .items(List.of(item))
                .build();

        when(itemRequestService.getItemRequestById(1L)).thenReturn(itemRequest2);

        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("YandexPracticum"))
                .andExpect(jsonPath("$.requestorId").value(1))
                .andExpect(jsonPath("$.items[0].id").value(1))
                .andExpect(jsonPath("$.items[0].name").value("Yandex"))
                .andExpect(jsonPath("$.items[0].description").value("YandexPracticum"))
                .andExpect(jsonPath("$.items[0].available").value(true))
                .andExpect(jsonPath("$.items[0].ownerId").value(1));
    }
}
