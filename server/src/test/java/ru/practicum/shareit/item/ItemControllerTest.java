package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.interfaces.ItemService;
import ru.practicum.shareit.item.model.*;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ItemService itemService;

    CreateItemDto item1;
    ItemDto item2;
    ItemDto item3;

    @BeforeEach
    void beforeEach() {
        item1 = CreateItemDto.builder().name("Yandex").description("YandexPracticum").available(true).build();
        item2 = ItemDto.builder()
                .id(1L)
                .name("Yandex")
                .description("YandexPracticum")
                .available(true)
                .owner(User.builder().id(1L).build())
                .build();
        item3 = ItemDto.builder()
                .id(2L)
                .name("Yandex2")
                .description("YandexPracticum2")
                .available(true)
                .owner(User.builder().id(1L).build())
                .build();
    }

    @Test
    void shouldReturnCreatedWhenCreateItem() throws Exception {
        ItemDtoRequest item4 = ItemDtoRequest.builder()
                .id(1L)
                .name("Yandex")
                .description("YandexPracticum")
                .available(true)
                .ownerId(1L)
                .build();

        when(itemService.createItem(1L, item1)).thenReturn(item4);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Yandex"))
                .andExpect(jsonPath("$.description").value("YandexPracticum"))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.ownerId").value(1L));
    }

    @Test
    void shouldReturnOkWhenUpdateItem() throws Exception {
        UpdateItemDto itemUpdate = UpdateItemDto.builder().name("Yandex").description("YandexPracticum").available(true).build();
        when(itemService.updateItem(1L, itemUpdate, 1L)).thenReturn(item2);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Yandex"))
                .andExpect(jsonPath("$.description").value("YandexPracticum"))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.owner").value(User.builder().id(1L).build()));
    }

    @Test
    void shouldReturnOkWhenGetItemById() throws Exception {
        ExtendedItemDto itemEx = ExtendedItemDto.builder()
                .id(1L)
                .name("Yandex")
                .description("YandexPracticum")
                .available(true)
                .owner(User.builder().id(1L).build())
                .build();
        when(itemService.getItemById(1L)).thenReturn(itemEx);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Yandex"))
                .andExpect(jsonPath("$.description").value("YandexPracticum"))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.owner").value(User.builder().id(1L).build()));
    }

    @Test
    void shouldReturnOkWhenGetAllItemsByOwner() throws Exception {
        when(itemService.getAllItemsByOwner(1L)).thenReturn(List.of(item2, item3));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Yandex2"))
                .andExpect(jsonPath("$[1].description").value("YandexPracticum2"))
                .andExpect(jsonPath("$[1].available").value(true))
                .andExpect(jsonPath("$[1].owner").value(User.builder().id(1L).build()));
    }

    @Test
    void shouldReturnOkWhenSearchItems() throws Exception {
        when(itemService.searchItems("yandex")).thenReturn(List.of(item2, item3));

        mockMvc.perform(get("/items/search")
                        .param("text", "yandex")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Yandex2"))
                .andExpect(jsonPath("$[1].description").value("YandexPracticum2"))
                .andExpect(jsonPath("$[1].available").value(true))
                .andExpect(jsonPath("$[1].owner").value(User.builder().id(1L).build()));
    }

    @Test
    void shouldReturnCreatedWhenCreateComment() throws Exception {
        CommentDto comment = CommentDto.builder().text("YandexPracticum").build();
        CommentDto newComment = CommentDto.builder()
                .id(1L)
                .text("YandexPracticum")
                .item(item2)
                .authorName("Yandex")
                .build();

        when(itemService.addCommentToItem(1L, 1L, comment)).thenReturn(newComment);

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(comment)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("YandexPracticum"))
                .andExpect(jsonPath("$.authorName").value("Yandex"));
    }
}
