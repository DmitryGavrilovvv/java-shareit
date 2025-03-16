package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createBooking(@Valid @RequestBody BookingDto requestDto,
                                                @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        return bookingClient.createBooking(userId, requestDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBooking(@RequestParam Boolean approved,
                                                @PathVariable Long bookingId,
                                                @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрос на подтверждение или отклонение бронирования с id = {}", bookingId);
        return bookingClient.updateBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@PathVariable Long bookingId,
                                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрос на получение бронирования с id = {} от пользователя с id = {}", bookingId, userId);
        return bookingClient.getBookingById(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingsByUser(@RequestParam(defaultValue = "ALL") BookingState state,
                                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрос на получение списка бронирований пользователя с id = {} с параметром '{}'", userId, state);
        return bookingClient.getBookingsByUser(userId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsByOwner(@RequestParam(defaultValue = "ALL") BookingState state,
                                                     @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрос на получение списка бронирований владельца с id = {} с параметром '{}'", userId, state);
        return bookingClient.getBookingsByOwner(userId, state);
    }
}
