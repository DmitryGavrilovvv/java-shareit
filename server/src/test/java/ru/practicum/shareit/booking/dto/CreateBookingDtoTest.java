package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.model.CreateBookingDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CreateBookingDtoTest {

    @Autowired
    private JacksonTester<CreateBookingDto> json;

    @Test
    void testSerialize() throws Exception {
        var dto = CreateBookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.of(2025, 3, 9, 12, 0))
                .end(LocalDateTime.of(2025, 3, 9, 14, 0))
                .build();

        var result = json.write(dto);

        assertThat(result)
                .hasJsonPath("$.itemId")
                .hasJsonPath("$.start")
                .hasJsonPath("$.end");
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(dto.getItemId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2025-03-09T12:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2025-03-09T14:00:00");
    }
}
