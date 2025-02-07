package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    public ResponseEntity<?> ThrowableHandler(Throwable exception) {
        ErrorResponse errorResponse = new ErrorResponse("Ошибка: ", exception.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler
    public ResponseEntity<?> ShareItExceptionHandler(ShareItException exception) {
        ErrorResponse errorResponse = new ErrorResponse("Ошибка: ", exception.getMessage());
        return ResponseEntity.status(exception.getStatus()).body(errorResponse);
    }

    @ExceptionHandler
    public ResponseEntity<?> ValidationHandler(MethodArgumentNotValidException exception) {
        ErrorResponse errorResponse = new ErrorResponse("Ошибка: ", exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
