package ru.practicum.shareit.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ShareItException extends RuntimeException {
    private final HttpStatus status;

    public ShareItException(ShareItExceptionCodes code, Object... params) {
        super(String.format(code.getMessage(), params));
        this.status = code.getStatus();
    }
}
