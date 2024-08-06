package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingFullDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.ItemAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;

    @Transactional
    @Override
    public BookingFullDto create(BookingDto bookingDto, long userId) {
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

        Booking booking = bookingMapper.dtoToBooking(bookingDto, item, user);
        booking.setStatus(BookingStatus.WAITING);

        booking = bookingRepository.save(booking);

        return bookingMapper.bookingToFullDto(
                booking,
                itemMapper.itemToDTO(item),
                userMapper.userToDto(user)
        );
    }

    @Transactional
    @Override
    public BookingFullDto approve(long bookingId, boolean approved, long userId) {
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

        return bookingMapper.bookingToFullDto(
                booking,
                itemMapper.itemToDTO(booking.getItem()),
                userMapper.userToDto(booking.getBooker())
        );
    }

    @Transactional(readOnly = true)
    @Override
    // (включая его статус). Может быть выполнено либо автором бронирования, либо владельцем вещи
    public BookingFullDto getById(long bookingId, long userId) {
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

        return bookingMapper.bookingToFullDto(
                booking,
                itemMapper.itemToDTO(booking.getItem()),
                userMapper.userToDto(booking.getBooker())
        );
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<BookingFullDto> getByUser(BookingState state, long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.debug("GET BY USER. Пользователь с айди {} не найден", userId);
                    return new NotFoundException("Пользователь с id=" + userId + " не существует");
                });

        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = switch (state) {
            case PAST -> bookingRepository.findBookingsByBookerIdAndEndBeforeOrderByStartDesc(userId, now);
            case FUTURE -> bookingRepository.findBookingsByBookerIdAndStartAfterOrderByStartDesc(userId, now);
            case CURRENT -> bookingRepository.findCurrentBookings(userId, now);
            case WAITING ->
                    bookingRepository.findBookingsByBookerIdAndStatusEqualsOrderByStartDesc(userId, BookingStatus.WAITING);
            case REJECTED ->
                    bookingRepository.findBookingsByBookerIdAndStatusEqualsOrderByStartDesc(userId, BookingStatus.REJECTED);
            default -> bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
        };

        return bookings.stream()
                .map(b -> {
                    ItemDto itemDto = itemMapper.itemToDTO(b.getItem());
                    UserDto userDto = userMapper.userToDto(b.getBooker());
                    return bookingMapper.bookingToFullDto(b, itemDto, userDto);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<BookingFullDto> getByOwner(BookingState state, long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.debug("GET BY OWNER. Пользователь с айди {} не найден", userId);
                    return new NotFoundException("Пользователь с id=" + userId + " не существует");
                });

        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = switch (state) {
            case PAST -> bookingRepository.findBookingsByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, now);
            case FUTURE -> bookingRepository.findBookingsByItemOwnerIdAndStartAfterOrderByStartDesc(userId, now);
            case CURRENT -> bookingRepository.findCurrentBookingsByOwner(userId, now);
            case WAITING ->
                    bookingRepository.findBookingsByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
            case REJECTED ->
                    bookingRepository.findBookingsByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
            default -> bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId);
        };

        return bookings.stream()
                .map(b -> {
                    ItemDto itemDto = itemMapper.itemToDTO(b.getItem());
                    UserDto userDto = userMapper.userToDto(b.getBooker());
                    return bookingMapper.bookingToFullDto(b, itemDto, userDto);
                })
                .collect(Collectors.toList());
    }
}
