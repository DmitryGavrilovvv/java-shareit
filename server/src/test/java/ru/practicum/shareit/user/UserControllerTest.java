package ru.practicum.shareit.user;

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
import ru.practicum.shareit.user.interfaces.UserService;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.model.UserUpdateDto;

import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    UserService userService;

    UserDto user1;
    UserDto user2;

    @BeforeEach
    void beforeEach() {
        user1 = UserDto.builder().name("Yandex").email("yandex@practicum.ru").build();
        user2 = UserDto.builder().id(1L).name("Yandex2").email("yandex2@practicum.ru").build();
    }

    @Test
    void shouldReturnOkWhenGetAllUsers() throws Exception {
        user1.setId(2L);
        List<UserDto> users = List.of(user2, user1);

        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Yandex"))
                .andExpect(jsonPath("$[1].email").value("yandex@practicum.ru"));
    }

    @Test
    void shouldReturnCreatedWhenCreateUser() throws Exception {
        user2 = UserDto.builder().id(1L).name("Yandex").email("yandex@practicum.ru").build();

        when(userService.createUser(user1)).thenReturn(user2);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Yandex"))
                .andExpect(jsonPath("$.email").value("yandex@practicum.ru"));
    }

    @Test
    void shouldReturnOkWhenUpdateUser() throws Exception {
        UserUpdateDto userUpdateDto = UserUpdateDto.builder().name(user1.getName()).email(user1.getEmail()).build();
        when(userService.updateUser(1L, userUpdateDto)).thenReturn(user2);

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Yandex2"))
                .andExpect(jsonPath("$.email").value("yandex2@practicum.ru"));
    }

    @Test
    void shouldReturnOkWhenGetUserById() throws Exception {
        when(userService.getUserById(1L)).thenReturn(user2);

        mockMvc.perform(get("/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Yandex2"))
                .andExpect(jsonPath("$.email").value("yandex2@practicum.ru"));
    }

    @Test
    void shouldReturnNoContentWhenDeleteUser() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
