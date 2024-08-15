package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnauthorizedModification;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository requestRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;
    private final BookingMapper bookingMapper;

    @Transactional
    @Override
    public ItemDto create(ItemDto itemDto, long ownerId) {
        User user = userRepository.findById(ownerId)
                .orElseThrow(() -> {
                    log.debug("CREATE ITEM. Пользователь с айди {} не найден", ownerId);
                    return new NotFoundException("Пользователь с id=" + ownerId + " не существует");
                });
        ItemRequest itemRequest = null;
        if (itemDto.getRequestId() != null) {
            itemRequest = requestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> {
                        log.debug("CREATE ITEM. Запрос на вещь с айди {} не найден", itemDto.getRequestId());
                        return new NotFoundException("Запрос с id=" + itemDto.getRequestId() + " не существует");
                    });
        }
        Item item = itemMapper.dtoToItem(itemDto, user, itemRequest);
        return itemMapper.itemToDTO(itemRepository.save(item));
    }

    @Transactional
    @Override
    public ItemDto update(ItemDto itemDto, long ownerId) {
        long itemId = itemDto.getId();
        Item updatedItem = itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.debug("UPDATE ITEM By ID={}. Вещь с айди {} не найден", itemId, itemId);
                    return new NotFoundException("Вещь с id=" + itemId + " не существует");
                });
        if (updatedItem.getOwner().getId() != ownerId) {
            throw new UnauthorizedModification("Менять описание вещи может только владелец");
        }

        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            updatedItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            updatedItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            updatedItem.setAvailable(itemDto.getAvailable());
        }

        return itemMapper.itemToDTO(updatedItem);
    }

    @Transactional(readOnly = true)
    @Override
    public ItemInfoDto getById(long itemId, long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.debug("GET ITEM By ID={}. Вещь с айди {} не найден", itemId, itemId);
                    return new NotFoundException("Вещь с id=" + itemId + " не существует");
                });

        List<CommentDto> commentsDtos = commentRepository.findByItemId(itemId).stream()
                .map(commentMapper::commentToDto)
                .collect(Collectors.toList());

        List<Booking> bookings = null;
        if (item.getOwner().getId() == userId) {
            bookings = bookingRepository.findByItemIdAndStatus(itemId, BookingStatus.APPROVED);
        }

        if (bookings == null) {
            return itemMapper.itemToInfoDto(item, null, null, commentsDtos);
        }

        return makeItemInfoDto(item, commentsDtos, bookings);
    }

    private ItemInfoDto makeItemInfoDto(Item item, List<CommentDto> commentsDtos, List<Booking> bookings) {
        LocalDateTime nowTime = LocalDateTime.now();
        Optional<Booking> lastOpt = bookings.stream()
                .filter(booking -> booking.getEnd().isBefore(nowTime))
                .max(Comparator.comparing(Booking::getEnd));
        Optional<Booking> nextOpt = bookings.stream()
                .filter(booking -> booking.getStart().isAfter(nowTime))
                .min(Comparator.comparing(Booking::getStart));
        BookingDto last = lastOpt.map(bookingMapper::bookingToDto).orElse(null);
        BookingDto next = nextOpt.map(bookingMapper::bookingToDto).orElse(null);

        return itemMapper.itemToInfoDto(item, last, next, commentsDtos);
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<ItemInfoDto> getByOwner(long ownerId) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> {
                    log.debug("GET ITEMS BY OWNER. Пользователь с айди {} не найден", ownerId);
                    return new NotFoundException("Пользователь с id=" + ownerId + " не существует");
                });

        // выгружаем вещи (один запрос)
        Map<Long, Item> items = itemRepository.findByOwnerId(ownerId)
                .stream()
                .collect(Collectors.toMap(Item::getId, Function.identity()));

        // выгружаем комментарии (ещё один запрос)
        Map<Long, List<CommentDto>> comments = commentRepository.findByItemIdIn(items.keySet()).stream()
                .map(commentMapper::commentToDto)
                .collect(Collectors.groupingBy(CommentDto::getItemId));
        // выгружаем бронирования (ещё один запрос)
        Map<Long, List<Booking>> bookings = bookingRepository.findByItemIdInAndStatus(items.keySet(), BookingStatus.APPROVED)
                .stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));

        // готовим окончательный результат из полученных данных (нет обращений к БД)
        return items.values()
                .stream()
                .map(item -> makeItemInfoDto(
                        item,
                        comments.getOrDefault(item.getId(), Collections.emptyList()),
                        bookings.getOrDefault(item.getId(), Collections.emptyList())
                ))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<ItemDto> search(String text) {
        return itemRepository.search(text.toLowerCase()).stream()
                .map(itemMapper::itemToDTO).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public CommentDto addComment(CommentCreateDto commentDto, Long authorId) {
        List<Booking> booking = bookingRepository.findByItemIdAndBookerIdAndEndBefore(
                commentDto.getItemId(),
                authorId,
                LocalDateTime.now());

        if (booking.isEmpty()) {
            throw new AccessException("Комментарии доступны пользователям, которые пользовались вещью");
        }

        Comment comment = commentMapper.createdDtoToComment(commentDto, booking.getFirst().getItem(), booking.getFirst().getBooker());
        return commentMapper.commentToDto(commentRepository.save(comment));
    }
}
