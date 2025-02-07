package ru.practicum.shareit.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ShareItExceptionCodes {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "Пользователь с id = %s не найден"),
    ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "Вещь с id = %s не найдена"),
    EMPTY_ITEM_ID(HttpStatus.NOT_FOUND, "Id не должен быть пустым"),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "e-mail = %s уже используется"),
    NOT_OWNER_UPDATE(HttpStatus.NOT_FOUND, "Редактировать может только владелец вещи");

    private final HttpStatus status;
    private final String message;

    ShareItExceptionCodes(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
