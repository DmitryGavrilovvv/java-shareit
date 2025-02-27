package ru.practicum.shareit.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ShareItExceptionCodes {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "Пользователь с id = %s не найден"),
    BOOKING_NOT_FOUND(HttpStatus.NOT_FOUND, "Бронь с id = %s не найдена"),
    ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "Вещь с id = %s не найдена"),
    EMPTY_ITEM_ID(HttpStatus.NOT_FOUND, "Id не должен быть пустым"),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "e-mail = %s уже используется"),
    NOT_OWNER_UPDATE(HttpStatus.NOT_FOUND, "Редактировать может только владелец вещи"),
    EMPTY_USER_ID(HttpStatus.NOT_FOUND, "id должен быть указан"),
    EMPTY_USER_NAME(HttpStatus.NOT_FOUND, "Имя пользователя не может быть пустым"),
    EMPTY_USER_EMAIL(HttpStatus.NOT_FOUND, "email не может быть пустым"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "Доступ к брони с id = %s запрещён"),
    ITEM_NOT_AVAILABLE(HttpStatus.BAD_REQUEST, "Предмет с id = %s не доступен для бронирования"),
    COMMIT_DENIED(HttpStatus.BAD_REQUEST, "Пользователь с id = %s не может оставить комментарии к вещи с id = %s");

    private final HttpStatus status;
    private final String message;

    ShareItExceptionCodes(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
