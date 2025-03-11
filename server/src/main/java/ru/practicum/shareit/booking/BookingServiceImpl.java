package ru.practicum.shareit.booking;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.enums.BookingSearchState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.interfaces.BookingService;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingDto;
import ru.practicum.shareit.booking.model.CreateBookingDto;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.BookingException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
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
        if (booking.getStart().isAfter(booking.getEnd())) {
            throw new ValidationException("Время конца раньше времени начала");
        }
        User booker = userRepository.findById(bookerId).orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %d не найден", booking.getBooker().getId())));
        Long itemId = booking.getItem().getId();
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException(String.format("Вещь с id = %d не найдена", booking.getItem().getId())));
        if (!item.getAvailable()) {
            throw new BookingException(String.format("Вещь с id = %d недоступна для бронирования", booking.getItem().getId()));
        }
        booking.setBooker(booker);
        booking.setItem(item);
        return BookingMapper.mapToBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBooking(long bookingId, long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Бронь с id = %d не найдена", bookingId)));
        if (booking.getBooker().getId() != userId && booking.getItem().getOwner().getId() != userId) {
            throw new AccessDeniedException(String.format("Доступ к брони с id = %d запрещен", bookingId));
        }
        return BookingMapper.mapToBookingDto(booking);
    }

    @Override
    public Collection<BookingDto> getBookings(long bookerId, BookingSearchState state) {
        if (!userRepository.existsById(bookerId)) {
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", bookerId));
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
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", ownerId));
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
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException(String.format("Бронь с id = %d не найдена", bookingId)));

        if (booking.getItem().getOwner().getId() != ownerId) {
            throw new AccessDeniedException(String.format("Подтверждение брони с id = %d пользователем с id = %d запрещено", bookingId, ownerId));
        }

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        return BookingMapper.mapToBookingDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDto updateBooking(Long userId, Long bookingId, Boolean approved) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %s не найден", userId)));
        Booking booking = BookingMapper.mapToBooking(getBooking(bookingId, userId));

        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new ValidationException("Бронирование уже подтверждено или отклонено");
        }
        if (!userId.equals(booking.getItem().getOwner().getId())) {
            throw new ValidationException("Подтверждение или отказ бронирования может делать только владелец");
        }
        log.debug("Обновление бронирования вещи с id = {} пользователя с id = {}", booking.getItem().getId(), userId);
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return BookingMapper.mapToBookingDto(bookingRepository.save(booking));
    }
}
