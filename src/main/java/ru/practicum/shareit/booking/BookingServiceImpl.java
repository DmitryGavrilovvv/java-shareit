package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.enums.BookingSearchState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.interfaces.BookingService;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingDto;
import ru.practicum.shareit.booking.model.CreateBookingDto;
import ru.practicum.shareit.exception.ShareItException;
import ru.practicum.shareit.exception.ShareItExceptionCodes;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public BookingDto createBooking(long bookerId, CreateBookingDto bookingDto) {
        Booking booking = BookingMapper.mapToBooking(bookerId, bookingDto);
        User booker = userRepository.findById(bookerId).orElseThrow(() -> new ShareItException(ShareItExceptionCodes.USER_NOT_FOUND, bookerId));
        Long itemId = booking.getItem().getId();
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ShareItException(ShareItExceptionCodes.ITEM_NOT_FOUND, itemId));
        if (!item.getAvailable()) {
            throw new ShareItException(ShareItExceptionCodes.ITEM_NOT_AVAILABLE, itemId);
        }
        booking.setBooker(booker);
        booking.setItem(item);
        return BookingMapper.mapToBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBooking(long bookingId, long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ShareItException(ShareItExceptionCodes.BOOKING_NOT_FOUND, bookingId));
        if (booking.getBooker().getId() != userId && booking.getItem().getOwner().getId() != userId) {
            throw new ShareItException(ShareItExceptionCodes.ACCESS_DENIED, bookingId);
        }
        return BookingMapper.mapToBookingDto(booking);
    }

    @Override
    public Collection<BookingDto> getBookings(long bookerId, BookingSearchState state) {
        if (!userRepository.existsById(bookerId)) {
            throw new ShareItException(ShareItExceptionCodes.BOOKING_NOT_FOUND, bookerId);
        }
        Collection<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findByBookerIdOrderByStartDesc(bookerId);
            case PAST -> bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(bookerId, LocalDateTime.now());
            case FUTURE -> bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(bookerId, LocalDateTime.now());
            case CURRENT ->
                    bookingRepository.findByBookerIdAndStartAfterAndEndBeforeOrderByStartDesc(bookerId, LocalDateTime.now(), LocalDateTime.now());
            case WAITING -> bookingRepository.findByBookerIdAndStatusOrderByStartDesc(bookerId, BookingStatus.WAITING);
            case REJECTED ->
                    bookingRepository.findByBookerIdAndStatusOrderByStartDesc(bookerId, BookingStatus.REJECTED);
        };
        return bookings.stream().map(BookingMapper::mapToBookingDto).toList();
    }

    @Override
    public Collection<BookingDto> getOwnerBookings(long ownerId, BookingSearchState state) {
        if (!userRepository.existsById(ownerId)) {
            throw new ShareItException(ShareItExceptionCodes.USER_NOT_FOUND, ownerId);
        }

        Collection<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId);
            case PAST -> bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId, LocalDateTime.now());
            case FUTURE ->
                    bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(ownerId, LocalDateTime.now());
            case CURRENT ->
                    bookingRepository.findByItemOwnerIdAndStartAfterAndEndBeforeOrderByStartDesc(ownerId, LocalDateTime.now(), LocalDateTime.now());
            case WAITING ->
                    bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.WAITING);
            case REJECTED ->
                    bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.REJECTED);
        };
        return bookings.stream().map(BookingMapper::mapToBookingDto).toList();
    }

    @Override
    public BookingDto approveBooking(long bookingId, long ownerId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new ShareItException(ShareItExceptionCodes.BOOKING_NOT_FOUND, bookingId));

        if (booking.getItem().getOwner().getId() != ownerId) {
            throw new ShareItException(ShareItExceptionCodes.ACCESS_DENIED, bookingId);
        }

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        return BookingMapper.mapToBookingDto(bookingRepository.save(booking));
    }
}
