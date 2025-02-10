package ru.practicum.shareit.booking.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Builder
@Data
public class Booking {
    private final Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private final Item item;
    private final User booker;
    private BookingStatus status;
}
