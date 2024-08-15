package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import static ru.practicum.shareit.constants.UserIdHttpHeader.USER_ID_HEADER;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader(USER_ID_HEADER) long userId,
                                                @RequestBody @Valid BookItemRequestDto requestDto) {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        return bookingClient.createBooking(userId, requestDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@PathVariable long bookingId,
                                                 @RequestHeader(USER_ID_HEADER) long userId,
                                                 @RequestParam boolean approved) {
        log.info("Approve booking {}, userId={}, approve={}", bookingId, userId, approved);
        return bookingClient.approveBooking(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@RequestHeader(USER_ID_HEADER) long userId,
                                                 @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBookingById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingByUser(@RequestParam(name = "state", defaultValue = "ALL") String stateValue,
                                                   @RequestHeader(USER_ID_HEADER) long userId) {
        BookingState.from(stateValue)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateValue));
        log.info("Get bookings by user {} with state={}", userId, stateValue);
        return bookingClient.getBookingsByUser(stateValue, userId);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingByOwner(@RequestParam(name = "state", defaultValue = "ALL") String stateValue,
                                                    @RequestHeader(USER_ID_HEADER) long userId) {
        BookingState.from(stateValue)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateValue));
        log.info("Get bookings by owner {} with state={}", userId, stateValue);
        return bookingClient.getBookingsByOwner(stateValue, userId);
    }
}
