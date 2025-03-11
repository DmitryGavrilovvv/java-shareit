package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.model.ItemDtoRequest;
import ru.practicum.shareit.request.model.ItemRequestDto;
import ru.practicum.shareit.user.model.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void testSerialize() throws Exception {
        LocalDateTime time = LocalDateTime.of(2025, 3, 9, 12, 0);
        ItemDtoRequest item = ItemDtoRequest.builder()
                .id(1L)
                .name("Yandex")
                .description("YandexPracticum")
                .available(true)
                .ownerId(1L)
                .requestId(1L)
                .build();
        var dto = ItemRequestDto.builder()
                .id(1L)
                .description("Yandex")
                .requestor(UserDto.builder().id(1L).build())
                .created(time)
                .items(List.of(item))
                .build();

        var result = json.write(dto);

        assertThat(result)
                .hasJsonPath("$.id")
                .hasJsonPath("$.description")
                .hasJsonPath("$.requestor.id")
                .hasJsonPath("$.created")
                .hasJsonPath("$.items");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(dto.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(dto.getDescription());
        assertThat(result).extractingJsonPathNumberValue("$.requestor.id")
                .isEqualTo(dto.getRequestor().getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2025-03-09T12:00:00");
        assertThat(result).extractingJsonPathNumberValue("$.items[0].id")
                .isEqualTo(dto.getItems().getFirst().getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.items[0].name")
                .isEqualTo(dto.getItems().getFirst().getName());
        assertThat(result).extractingJsonPathStringValue("$.items[0].description")
                .isEqualTo(dto.getItems().getFirst().getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.items[0].available")
                .isEqualTo(dto.getItems().getFirst().getAvailable());
        assertThat(result).extractingJsonPathNumberValue("$.items[0].ownerId")
                .isEqualTo(dto.getItems().getFirst().getOwnerId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.items[0].requestId")
                .isEqualTo(dto.getItems().getFirst().getRequestId().intValue());
    }
}
