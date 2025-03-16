package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.BookingDto;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.request.model.ItemRequestDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void testSerialize() throws Exception {
        UserDto user = UserDto.builder().id(1L).name("Name").email("yandex@practicum.ru").build();
        ItemDto item = ItemDto.builder()
                .id(1L)
                .name("Yandex")
                .description("YandexPracticum")
                .available(true)
                .owner(User.builder().id(1L).build())
                .request(ItemRequestDto.builder().id(1L).build())
                .build();
        var dto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2025, 3, 9, 12, 0))
                .end(LocalDateTime.of(2025, 3, 9, 14, 0))
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .build();

        var result = json.write(dto);

        assertThat(result)
                .hasJsonPath("$.id")
                .hasJsonPath("$.start")
                .hasJsonPath("$.end")
                .hasJsonPath("$.item")
                .hasJsonPath("$.booker")
                .hasJsonPath("$.status");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(dto.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2025-03-09T12:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2025-03-09T14:00:00");
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(item.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo(item.getName());
        assertThat(result).extractingJsonPathStringValue("$.item.description")
                .isEqualTo(item.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.item.available").isEqualTo(item.getAvailable());
        assertThat(result).extractingJsonPathNumberValue("$.item.owner.id")
                .isEqualTo(item.getOwner().getId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.item.request.id")
                .isEqualTo(item.getRequest().getId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(user.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo(user.getName());
        assertThat(result).extractingJsonPathStringValue("$.booker.email").isEqualTo(user.getEmail());
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(dto.getStatus().name());
    }
}
