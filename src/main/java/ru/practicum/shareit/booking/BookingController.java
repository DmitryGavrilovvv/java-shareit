package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.enums.BookingSearchState;
import ru.practicum.shareit.booking.interfaces.BookingService;
import ru.practicum.shareit.booking.model.BookingDto;
import ru.practicum.shareit.booking.model.CreateBookingDto;

import java.util.Collection;

@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@RestController
public final class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto createBooking(@RequestHeader(name = "X-Sharer-User-Id") long bookerId, @RequestBody @Valid CreateBookingDto dto) {
        return bookingService.createBooking(bookerId, dto);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader(name = "X-Sharer-User-Id") long userId, @PathVariable long bookingId) {
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping
    public Collection<BookingDto> getBookings(@RequestHeader(name = "X-Sharer-User-Id") long bookerId, @RequestParam(defaultValue = "ALL") BookingSearchState state) {
        return bookingService.getBookings(bookerId, state);
    }

    @GetMapping("/owner")
    public Collection<BookingDto> getOwnerBookings(@RequestHeader(name = "X-Sharer-User-Id") long ownerId, @RequestParam(defaultValue = "ALL") BookingSearchState state) {
        return bookingService.getOwnerBookings(ownerId, state);
    }

    @PatchMapping(value = "/{bookingId}")
    public BookingDto approveBooking(@RequestHeader(name = "X-Sharer-User-Id") long ownerId, @PathVariable long bookingId, @RequestParam boolean approved) {
        return bookingService.approveBooking(bookingId, ownerId, approved);
    }
}
