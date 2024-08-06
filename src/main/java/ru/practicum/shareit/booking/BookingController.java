package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingFullDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.Collection;

import static ru.practicum.shareit.constants.UserIdHttpHeader.USER_ID_HEADER;


@SuppressWarnings("checkstyle:Regexp")
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingFullDto create(@Validated @RequestBody BookingDto bookingDto, @RequestHeader(USER_ID_HEADER) long userId) {
        log.info("POST /bookings request: {}", bookingDto);
        BookingFullDto createdBooking = bookingService.create(bookingDto, userId);
        log.info("POST /bookings response: {}", createdBooking);
        return createdBooking;
    }

    @PatchMapping("/{bookingId}")
    public BookingFullDto approve(@PathVariable long bookingId,
                                  @RequestParam boolean approved,
                                  @RequestHeader(USER_ID_HEADER) long userId) {
        log.info("PATCH /bookings/{}/{} request", bookingId, approved);
        BookingFullDto approvedBooking = bookingService.approve(bookingId, approved, userId);
        log.info("PATCH /bookings/{}/{} response: {}", bookingId, approved, approvedBooking);
        return approvedBooking;
    }

    @GetMapping("/{bookingId}")
    public BookingFullDto getById(@PathVariable long bookingId, @RequestHeader(USER_ID_HEADER) long userId) {
        log.info("GET /bookings/{} request", bookingId);
        BookingFullDto booking = bookingService.getById(bookingId, userId);
        log.info("GET /bookings/{} response: {}", bookingId, booking);
        return booking;
    }

    @GetMapping
    public Collection<BookingFullDto> getByUser(@RequestParam(defaultValue = "ALL") String stateValue,
                                                @RequestHeader(USER_ID_HEADER) long userId) {
        log.info("GET BY USER /bookings/{} request", stateValue);
        BookingState state = BookingState.from(stateValue);
        if (state == null) {
            throw new IllegalArgumentException("Unknown state: " + stateValue);
        }
        Collection<BookingFullDto> bookings = bookingService.getByUser(state, userId);
        log.info("GET BY USER /bookings/{} response: {}", stateValue, bookings.size());
        return bookings;
    }

    @GetMapping("/owner")
    public Collection<BookingFullDto> getByOwner(@RequestParam(defaultValue = "ALL") BookingState state,
                                                 @RequestHeader(USER_ID_HEADER) long userId) {
        log.info("GET BY OWNER /bookings/owner/{} request", state);
        Collection<BookingFullDto> bookings = bookingService.getByOwner(state, userId);
        log.info("GET BY OWNER /bookings/owner/{} response: {}", state, bookings.size());
        return bookings;
    }
}
