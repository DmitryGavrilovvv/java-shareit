package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
@Data
public class CommentDto {
    private final long id;
    @NotBlank(message = "Содержимое комментария не может быть пустым")
    @Length(max = 512, message = "Содержимое комментария не может превышать 512 символов.")
    private final String text;
    private final ItemDto item;
    private final String authorName;
    private final LocalDateTime created;
}
