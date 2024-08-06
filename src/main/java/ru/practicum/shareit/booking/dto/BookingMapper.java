package ru.practicum.shareit.booking.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Component
public class BookingMapper {
    public BookingDto bookingToDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem().getId(),
                booking.getBooker().getId(),
                booking.getStatus()
        );
    }

    public BookingFullDto bookingToFullDto(Booking booking, ItemDto item, UserDto booker) {
        return new BookingFullDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                item,
                booker,
                booking.getStatus()
        );
    }

    public Booking dtoToBooking(BookingDto dto, Item item, User user) {
        return new Booking(
                dto.getId(),
                dto.getStart(),
                dto.getEnd(),
                item,
                user,
                dto.getStatus()
        );
    }
}
