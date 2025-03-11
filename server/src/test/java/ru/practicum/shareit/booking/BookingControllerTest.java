package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.enums.BookingSearchState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.interfaces.BookingService;
import ru.practicum.shareit.booking.model.BookingDto;
import ru.practicum.shareit.booking.model.CreateBookingDto;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.user.model.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class BookingControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    BookingService bookingService;

    CreateBookingDto booking1;
    BookingDto booking2;
    BookingDto booking3;

    @BeforeEach
    void beforeEach() {
        booking1 = CreateBookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();
        UserDto user = UserDto.builder().id(1L).name("Yandex").email("yandex@practicum.ru").build();
        ItemDto item = ItemDto.builder().id(1L).name("Yandex").description("YandexPracticum").available(true).build();
        booking2 = BookingDto.builder()
                .id(1L)
                .start(booking1.getStart())
                .end(booking1.getEnd())
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .build();
        booking3 = BookingDto.builder()
                .id(1L)
                .start(booking1.getStart())
                .end(booking1.getEnd())
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .build();
    }

    @Test
    void shouldReturnCreatedWhenCreateBooking() throws Exception {
        when(bookingService.createBooking(1L, booking1)).thenReturn(booking2);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booking1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.item.id").value(1))
                .andExpect(jsonPath("$.item.name").value("Yandex"))
                .andExpect(jsonPath("$.item.description").value("YandexPracticum"))
                .andExpect(jsonPath("$.booker.id").value(1))
                .andExpect(jsonPath("$.booker.name").value("Yandex"))
                .andExpect(jsonPath("$.booker.email").value("yandex@practicum.ru"))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void shouldReturnOkWhenUpdateBooking() throws Exception {
        when(bookingService.updateBooking(1L, 1L, true)).thenReturn(booking3);

        mockMvc.perform(put("/bookings/1")
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.item.id").value(1))
                .andExpect(jsonPath("$.item.name").value("Yandex"))
                .andExpect(jsonPath("$.item.description").value("YandexPracticum"))
                .andExpect(jsonPath("$.booker.id").value(1))
                .andExpect(jsonPath("$.booker.name").value("Yandex"))
                .andExpect(jsonPath("$.booker.email").value("yandex@practicum.ru"))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void shouldReturnOkWhenGetBookingById() throws Exception {
        when(bookingService.getBooking(1L, 1L)).thenReturn(booking2);

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.item.id").value(1))
                .andExpect(jsonPath("$.item.name").value("Yandex"))
                .andExpect(jsonPath("$.item.description").value("YandexPracticum"))
                .andExpect(jsonPath("$.booker.id").value(1))
                .andExpect(jsonPath("$.booker.name").value("Yandex"))
                .andExpect(jsonPath("$.booker.email").value("yandex@practicum.ru"))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void shouldReturnOkWhenGetBookingsByUser() throws Exception {
        List<BookingDto> bookings = List.of(booking2);

        when(bookingService.getBookings(1L, BookingSearchState.ALL)).thenReturn(bookings);

        mockMvc.perform(get("/bookings")
                        .param("state", "ALL")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].item.id").value(1))
                .andExpect(jsonPath("$[0].item.name").value("Yandex"))
                .andExpect(jsonPath("$[0].item.description").value("YandexPracticum"))
                .andExpect(jsonPath("$[0].booker.id").value(1))
                .andExpect(jsonPath("$[0].booker.name").value("Yandex"))
                .andExpect(jsonPath("$[0].booker.email").value("yandex@practicum.ru"))
                .andExpect(jsonPath("$[0].status").value("WAITING"));
    }

    @Test
    void shouldReturnOkWhenGetBookingsByOwner() throws Exception {
        List<BookingDto> bookings = List.of(booking2);

        when(bookingService.getOwnerBookings(1L, BookingSearchState.ALL)).thenReturn(bookings);

        mockMvc.perform(get("/bookings/owner")
                        .param("state", "ALL")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].item.id").value(1))
                .andExpect(jsonPath("$[0].item.name").value("Yandex"))
                .andExpect(jsonPath("$[0].item.description").value("YandexPracticum"))
                .andExpect(jsonPath("$[0].booker.id").value(1))
                .andExpect(jsonPath("$[0].booker.name").value("Yandex"))
                .andExpect(jsonPath("$[0].booker.email").value("yandex@practicum.ru"))
                .andExpect(jsonPath("$[0].status").value("WAITING"));
    }
}
