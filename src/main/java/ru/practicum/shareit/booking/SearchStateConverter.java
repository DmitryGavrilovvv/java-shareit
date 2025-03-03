package ru.practicum.shareit.booking;

import org.springframework.core.convert.converter.Converter;
import ru.practicum.shareit.booking.enums.BookingSearchState;

public class SearchStateConverter implements Converter<String, BookingSearchState> {

    @Override
    public BookingSearchState convert(String source) {
        return BookingSearchState.valueOf(source);
    }
}
