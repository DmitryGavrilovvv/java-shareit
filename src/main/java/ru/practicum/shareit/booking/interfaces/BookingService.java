package ru.practicum.shareit.booking.interfaces;

import ru.practicum.shareit.booking.enums.BookingSearchState;
import ru.practicum.shareit.booking.model.BookingDto;
import ru.practicum.shareit.booking.model.CreateBookingDto;

import java.util.Collection;

public interface BookingService {
    BookingDto createBooking(long bookerId, CreateBookingDto bookingDto);

    BookingDto getBooking(long bookingId, long userId);

    Collection<BookingDto> getBookings(long bookerId, BookingSearchState state);

    Collection<BookingDto> getOwnerBookings(long ownerId, BookingSearchState state);

    BookingDto approveBooking(long bookingId, long ownerId, boolean approved);
}
