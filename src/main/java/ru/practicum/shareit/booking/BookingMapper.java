package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingDto;
import ru.practicum.shareit.booking.model.CreateBookingDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {
    public static Booking mapToBooking(BookingDto bookingDto) {
        return Booking.builder()
                .item(ItemMapper.mapToItem(bookingDto.getItem()))
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .booker(UserMapper.mapToUser(bookingDto.getBooker()))
                .id(bookingDto.getId())
                .status(bookingDto.getStatus())
                .build();
    }

    public static BookingDto mapToBookingDto(Booking booking) {
        return BookingDto.builder()
                .item(ItemMapper.mapToItemDto(booking.getItem()))
                .start(booking.getStart())
                .end(booking.getEnd())
                .booker(UserMapper.mapToUserDto(booking.getBooker()))
                .id(booking.getId())
                .status(booking.getStatus())
                .build();
    }

    public static Booking mapToBooking(long bookerId, CreateBookingDto dto) {
        return Booking.builder()
                .start(dto.getStart())
                .end(dto.getEnd())
                .status(BookingStatus.WAITING)
                .booker(User.builder().id(bookerId).build())
                .item(Item.builder().id(dto.getItemId()).build())
                .build();
    }
}
