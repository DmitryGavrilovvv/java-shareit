package ru.practicum.shareit.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ShareItException extends RuntimeException {
    private final HttpStatus status;
    private final ShareItExceptionCodes shareItExceptionCodes;

    public ShareItException(ShareItExceptionCodes code, Object... params) {
        super(String.format(code.getMessage(), params));
        this.shareItExceptionCodes = code;
        this.status = code.getStatus();
    }
}
