package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingFullDto;

import java.util.Collection;

public interface BookingService {
    BookingFullDto create(BookingDto bookingDto, long userId);

    BookingFullDto approve(long bookingId, boolean approved, long userId);

    BookingFullDto getById(long bookingId, long userId);

    Collection<BookingFullDto> getByUser(BookingState state, long userId);

    Collection<BookingFullDto> getByOwner(BookingState state, long userId);
}
