package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    public ResponseEntity<?> throwableHandler(Throwable exception) {
        ErrorResponse errorResponse = new ErrorResponse(exception.toString(), exception.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler
    public ResponseEntity<?> shareItExceptionHandler(ShareItException exception) {
        ErrorResponse errorResponse = new ErrorResponse(exception.getShareItExceptionCodes().toString(), exception.getMessage());
        return ResponseEntity.status(exception.getStatus()).body(errorResponse);
    }

    @ExceptionHandler
    public ResponseEntity<?> validationHandler(MethodArgumentNotValidException exception) {
        ErrorResponse errorResponse = new ErrorResponse("Ошибка: ", exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
