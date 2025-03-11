package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.enums.BookingSearchState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.interfaces.BookingService;
import ru.practicum.shareit.booking.model.BookingDto;
import ru.practicum.shareit.booking.model.CreateBookingDto;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.BookingException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.interfaces.ItemService;
import ru.practicum.shareit.item.model.CreateItemDto;
import ru.practicum.shareit.item.model.ItemDtoRequest;
import ru.practicum.shareit.user.interfaces.UserService;
import ru.practicum.shareit.user.model.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingServiceImplTest {

    @Autowired
    ItemService itemService;

    @Autowired
    UserService userService;

    @Autowired
    BookingService bookingService;

    static UserDto user1;
    static UserDto user2;
    static CreateItemDto item1;

    @BeforeAll
    static void beforeAll() {
        user1 = UserDto.builder().name("Yandex").email("yandex@practicum.ru").build();
        user2 = UserDto.builder().name("Yandex2").email("yandex2@practicum.ru").build();
        item1 = CreateItemDto.builder().name("Yandex").description("YandexPracticum").available(true).build();
    }

    @Test
    void shouldCreateBooking() {
        UserDto user3 = userService.createUser(user1);
        UserDto user4 = userService.createUser(user2);
        ItemDtoRequest item = itemService.createItem(user3.getId(), item1);
        CreateBookingDto booking = CreateBookingDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();

        BookingDto newBooking = bookingService.createBooking(user4.getId(), booking);

        assertThat(newBooking.getId()).isNotNull();
        assertThat(newBooking.getStart()).isEqualTo(booking.getStart());
        assertThat(newBooking.getEnd()).isEqualTo(booking.getEnd());
        assertThat(newBooking.getItem().getId()).isEqualTo(booking.getItemId());
        assertThat(newBooking.getBooker().getId()).isEqualTo(user4.getId());
        assertThat(newBooking.getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void shouldThrowExceptionWhenIdIsNull() {
        UserDto user3 = userService.createUser(user1);
        ItemDtoRequest item = itemService.createItem(user3.getId(), item1);
        CreateBookingDto booking = CreateBookingDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();
        assertThatThrownBy(() -> bookingService.createBooking(null, booking))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void shouldThrowExceptionWhenItemIsNotAvailable() {
        UserDto user3 = userService.createUser(user1);
        UserDto user4 = userService.createUser(user2);
        CreateItemDto item2 = CreateItemDto.builder()
                .name("Yandex")
                .description("YandexPracticum")
                .available(false)
                .build();
        ItemDtoRequest item = itemService.createItem(user3.getId(), item2);
        CreateBookingDto booking = CreateBookingDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();

        assertThatThrownBy(() -> bookingService.createBooking(user4.getId(), booking))
                .isInstanceOf(BookingException.class);
    }

    @Test
    void shouldThrowExceptionWhenBookingStartEqualToEnd() {
        UserDto user3 = userService.createUser(user1);
        UserDto user4 = userService.createUser(user2);
        ItemDtoRequest item = itemService.createItem(user3.getId(), item1);
        CreateBookingDto booking = CreateBookingDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusHours(2))
                .end(LocalDateTime.now().plusHours(1))
                .build();

        assertThatThrownBy(() -> bookingService.createBooking(user4.getId(), booking))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void shouldThrowExceptionWhenOwnerCreateBooking() {
        UserDto user3 = UserDto.builder().id(1L).build();
        CreateBookingDto booking = CreateBookingDto.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();

        assertThatThrownBy(() -> bookingService.createBooking(user3.getId(), booking))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldUpdateBooking() {
        UserDto user3 = userService.createUser(user1);
        UserDto user4 = userService.createUser(user2);
        ItemDtoRequest item = itemService.createItem(user3.getId(), item1);
        CreateBookingDto booking = CreateBookingDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();

        BookingDto newBooking = bookingService.createBooking(user4.getId(), booking);
        BookingDto approvedBooking = bookingService.updateBooking(user3.getId(), newBooking.getId(), true);

        assertThat(approvedBooking.getId()).isEqualTo(newBooking.getId());
        assertThat(approvedBooking.getStart()).isEqualTo(newBooking.getStart());
        assertThat(approvedBooking.getEnd()).isEqualTo(newBooking.getEnd());
        assertThat(approvedBooking.getItem()).isEqualTo(newBooking.getItem());
        assertThat(approvedBooking.getBooker()).isEqualTo(user4);
        assertThat(approvedBooking.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void shouldThrowExceptionWhenUserIsNotOwnerWhenUpdateBooking() {
        UserDto user3 = userService.createUser(user1);
        UserDto user4 = userService.createUser(user2);
        ItemDtoRequest item = itemService.createItem(user3.getId(), item1);
        CreateBookingDto booking = CreateBookingDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();

        BookingDto newBooking = bookingService.createBooking(user4.getId(), booking);

        assertThatThrownBy(() -> bookingService.updateBooking(user4.getId(), newBooking.getId(), true))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void shouldThrowExceptionWhenBookingIsAlreadyApproved() {
        UserDto user3 = userService.createUser(user1);
        UserDto user4 = userService.createUser(user2);
        ItemDtoRequest item = itemService.createItem(user3.getId(), item1);
        CreateBookingDto booking = CreateBookingDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();

        BookingDto newBooking = bookingService.createBooking(user4.getId(), booking);
        bookingService.updateBooking(user3.getId(), newBooking.getId(), true);

        assertThatThrownBy(() -> bookingService.updateBooking(user3.getId(), newBooking.getId(), true))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void shouldChangeStatusToRejected() {
        UserDto user3 = userService.createUser(user1);
        UserDto user4 = userService.createUser(user2);
        ItemDtoRequest item = itemService.createItem(user3.getId(), item1);
        CreateBookingDto booking = CreateBookingDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();

        BookingDto newBooking = bookingService.createBooking(user4.getId(), booking);
        BookingDto rejectedBooking = bookingService.updateBooking(user3.getId(), newBooking.getId(), false);

        assertThat(rejectedBooking.getId()).isEqualTo(newBooking.getId());
        assertThat(rejectedBooking.getStart()).isEqualTo(newBooking.getStart());
        assertThat(rejectedBooking.getEnd()).isEqualTo(newBooking.getEnd());
        assertThat(rejectedBooking.getItem()).isEqualTo(newBooking.getItem());
        assertThat(rejectedBooking.getBooker()).isEqualTo(user4);
        assertThat(rejectedBooking.getStatus()).isEqualTo(BookingStatus.REJECTED);
    }

    @Test
    void shouldGetBookingById() {
        UserDto user3 = userService.createUser(user1);
        UserDto user4 = userService.createUser(user2);
        ItemDtoRequest item = itemService.createItem(user3.getId(), item1);
        CreateBookingDto booking = CreateBookingDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();

        BookingDto newBooking = bookingService.createBooking(user4.getId(), booking);
        BookingDto getBooking = bookingService.getBooking(newBooking.getId(), user4.getId());
        BookingDto getBookingOwner = bookingService.getBooking(newBooking.getId(), user3.getId());

        assertThat(getBooking.getId()).isEqualTo(newBooking.getId());
        assertThat(getBooking.getStart()).isEqualTo(newBooking.getStart());
        assertThat(getBooking.getEnd()).isEqualTo(newBooking.getEnd());
        assertThat(getBooking.getItem()).isEqualTo(newBooking.getItem());
        assertThat(getBooking.getBooker()).isEqualTo(user4);
        assertThat(getBooking.getStatus()).isEqualTo(BookingStatus.WAITING);

        assertThat(getBookingOwner.getId()).isEqualTo(newBooking.getId());
        assertThat(getBookingOwner.getStart()).isEqualTo(newBooking.getStart());
        assertThat(getBookingOwner.getEnd()).isEqualTo(newBooking.getEnd());
        assertThat(getBookingOwner.getItem()).isEqualTo(newBooking.getItem());
        assertThat(getBookingOwner.getBooker()).isEqualTo(user4);
        assertThat(getBookingOwner.getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void shouldThrowExceptionWhenUserIsNotOwnerOrBookerWhenGetBookingById() {
        UserDto user3 = userService.createUser(user1);
        UserDto user4 = userService.createUser(user2);
        UserDto user5 = UserDto.builder().name("Yandex3").email("yandex3@practicum.ru").build();
        UserDto user6 = userService.createUser(user5);
        ItemDtoRequest item = itemService.createItem(user3.getId(), item1);
        CreateBookingDto booking = CreateBookingDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();

        BookingDto newBooking = bookingService.createBooking(user4.getId(), booking);

        assertThatThrownBy(() -> bookingService.getBooking(newBooking.getId(), user6.getId()))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void shouldGetBookingsByUser() {
        UserDto user3 = userService.createUser(user1);
        UserDto user4 = userService.createUser(user2);
        ItemDtoRequest item = itemService.createItem(user3.getId(), item1);
        CreateBookingDto booking = CreateBookingDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();

        BookingDto newBooking = bookingService.createBooking(user4.getId(), booking);
        List<BookingDto> bookings = bookingService.getBookings(user4.getId(), BookingSearchState.ALL).stream().toList();

        assertThat(bookings.getFirst().getId()).isEqualTo(newBooking.getId());
        assertThat(bookings.getFirst().getStart()).isEqualTo(newBooking.getStart());
        assertThat(bookings.getFirst().getEnd()).isEqualTo(newBooking.getEnd());
        assertThat(bookings.getFirst().getItem()).isEqualTo(newBooking.getItem());
        assertThat(bookings.getFirst().getBooker()).isEqualTo(user4);
        assertThat(bookings.getFirst().getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void shouldGetBookingsByOwner() {
        UserDto user3 = userService.createUser(user1);
        UserDto user4 = userService.createUser(user2);
        ItemDtoRequest item = itemService.createItem(user3.getId(), item1);
        CreateBookingDto booking = CreateBookingDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();

        BookingDto newBooking = bookingService.createBooking(user4.getId(), booking);
        List<BookingDto> bookings = bookingService.getOwnerBookings(user3.getId(), BookingSearchState.ALL).stream().toList();

        assertThat(bookings.getFirst().getId()).isEqualTo(newBooking.getId());
        assertThat(bookings.getFirst().getStart()).isEqualTo(newBooking.getStart());
        assertThat(bookings.getFirst().getEnd()).isEqualTo(newBooking.getEnd());
        assertThat(bookings.getFirst().getItem()).isEqualTo(newBooking.getItem());
        assertThat(bookings.getFirst().getBooker()).isEqualTo(user4);
        assertThat(bookings.getFirst().getStatus()).isEqualTo(BookingStatus.WAITING);
    }
}
