package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerIdAndItemId(long bookerId, long itemId);

    List<Booking> findByBookerIdOrderByStartDesc(long bookerId);

    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(long bookerId, LocalDateTime before);

    List<Booking> findByBookerIdAndStartAfterAndEndBeforeOrderByStartDesc(long bookerId, LocalDateTime after, LocalDateTime before);

    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(long bookerId, LocalDateTime after);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(long bookerId, BookingStatus status);

    List<Booking> findByItemId(long itemId);

    List<Booking> findByItemIn(Collection<Item> items);

    List<Booking> findByItemOwnerIdOrderByStartDesc(long ownerId);

    List<Booking> findByItemOwnerIdAndEndBeforeOrderByStartDesc(long ownerId, LocalDateTime before);

    List<Booking> findByItemOwnerIdAndStartAfterAndEndBeforeOrderByStartDesc(long ownerId, LocalDateTime after, LocalDateTime before);

    List<Booking> findByItemOwnerIdAndStartAfterOrderByStartDesc(long ownerId, LocalDateTime after);

    List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(long ownerId, BookingStatus status);
}
