package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.request.model.ItemRequestDtoCreate;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoCreateTest {

    @Autowired
    private JacksonTester<ItemRequestDtoCreate> json;

    @Test
    void testSerialize() throws Exception {
        var dto = ItemRequestDtoCreate.builder().description("Yandex").build();

        var result = json.write(dto);

        assertThat(result).hasJsonPath("$.description");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(dto.getDescription());
    }
}
