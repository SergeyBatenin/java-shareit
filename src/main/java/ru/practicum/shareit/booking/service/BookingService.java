package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.Collection;

public interface BookingService {
    Booking create(BookingDto bookingDto, long userId);

    Booking approve(long bookingId, boolean approved, long userId);

    Booking getById(long bookingId, long userId);

    Collection<Booking> getByUser(BookingState state, long userId);

    Collection<Booking> getByOwner(BookingState state, long userId);
}
