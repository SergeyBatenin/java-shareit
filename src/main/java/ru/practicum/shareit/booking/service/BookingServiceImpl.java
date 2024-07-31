package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.ItemAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper mapper;

    @Transactional
    @Override
    public Booking create(BookingDto bookingDto, long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.debug("GET USER BOOKING CREATE. Пользователь с айди {} не найден", userId);
                    return new NotFoundException("Пользователь с id=" + userId + " не существует");
                });
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> {
                    log.debug("GET ITEM BOOKING CREATE. Предмет с айди {} не найден", userId);
                    return new NotFoundException("Предмет с id=" + userId + " не существует");
                });

        if (!item.getAvailable()) {
            throw new ItemAvailableException("Данный предмет недоступен для аренды сейчас");
        }

        Booking booking = mapper.dtoToBooking(bookingDto, item, user);
        booking.setStatus(BookingStatus.WAITING);

        return bookingRepository.save(booking);
    }

    @Transactional
    @Override
    public Booking approve(long bookingId, boolean approved, long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    log.debug("GET BOOKING. Запрос бронирования с айди {} не найден", bookingId);
                    return new NotFoundException("Запрос бронирования с id=" + bookingId + " не существует");
                });

        Long ownerId = booking.getItem().getOwner().getId();
        if (ownerId != userId) {
            throw new AccessException("Вы не являетесь владельцем предмета");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        return bookingRepository.save(booking);
    }

    @Transactional(readOnly = true)
    @Override
    // (включая его статус). Может быть выполнено либо автором бронирования, либо владельцем вещи
    public Booking getById(long bookingId, long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    log.debug("GET BOOKING. Запрос бронирования с айди {} не найден", bookingId);
                    return new NotFoundException("Запрос бронирования с id=" + bookingId + " не существует");
                });

        long bookerId = booking.getBooker().getId();
        long ownerId = booking.getItem().getOwner().getId();

        if (userId != bookerId && userId != ownerId) {
            throw new AccessException("У вас нет доступа к просмотру данного бронирования");
        }

        return booking;
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<Booking> getByUser(BookingState state, long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.debug("GET BY USER. Пользователь с айди {} не найден", userId);
                    return new NotFoundException("Пользователь с id=" + userId + " не существует");
                });

        LocalDateTime now = LocalDateTime.now();
        return switch (state) {
            case PAST -> bookingRepository.findBookingsByBookerIdAndEndBeforeOrderByStartDesc(userId, now);
            case FUTURE -> bookingRepository.findBookingsByBookerIdAndStartAfterOrderByStartDesc(userId, now);
            case CURRENT -> bookingRepository.findCurrentBookings(userId, now);
            case WAITING ->
                    bookingRepository.findBookingsByBookerIdAndStatusEqualsOrderByStartDesc(userId, BookingStatus.WAITING);
            case REJECTED ->
                    bookingRepository.findBookingsByBookerIdAndStatusEqualsOrderByStartDesc(userId, BookingStatus.REJECTED);
            default -> bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
        };
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<Booking> getByOwner(BookingState state, long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.debug("GET BY OWNER. Пользователь с айди {} не найден", userId);
                    return new NotFoundException("Пользователь с id=" + userId + " не существует");
                });

        LocalDateTime now = LocalDateTime.now();
        return switch (state) {
            case PAST -> bookingRepository.findBookingsByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, now);
            case FUTURE -> bookingRepository.findBookingsByItemOwnerIdAndStartAfterOrderByStartDesc(userId, now);
            case CURRENT -> bookingRepository.findCurrentBookingsByOwner(userId, now);
            case WAITING ->
                    bookingRepository.findBookingsByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
            case REJECTED ->
                    bookingRepository.findBookingsByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
            default -> bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId);
        };
    }
}
