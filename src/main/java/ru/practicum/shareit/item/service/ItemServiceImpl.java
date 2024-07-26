package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnauthorizedModification;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper mapper;

    @Transactional
    public ItemDto create(ItemDto itemDto, long ownerId) {
        User user = userRepository.findById(ownerId)
                .orElseThrow(() -> {
                    log.debug("CREATE ITEM. Пользователь с айди {} не найден", ownerId);
                    return new NotFoundException("Пользователь с id=" + ownerId + " не существует");
                });
        Item item = mapper.dtoToItem(itemDto, user);
        return mapper.itemToDTO(itemRepository.save(item));
    }

    @Transactional
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

        return mapper.itemToDTO(itemRepository.save(updatedItem));
    }

    public ItemDto getById(long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.debug("UPDATE ITEM By ID={}. Вещь с айди {} не найден", itemId, itemId);
                    return new NotFoundException("Вещь с id=" + itemId + " не существует");
                });

        return mapper.itemToDTO(item);
    }

    public Collection<ItemDto> getByOwner(long ownerId) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> {
                    log.debug("GET ITEMS BY OWNER. Пользователь с айди {} не найден", ownerId);
                    return new NotFoundException("Пользователь с id=" + ownerId + " не существует");
                });
        return itemRepository.findByOwnerId(ownerId).stream()
                .map(mapper::itemToDTO).collect(Collectors.toList());
    }

    public Collection<ItemDto> search(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.search(text.toLowerCase()).stream()
                .map(mapper::itemToDTO).collect(Collectors.toList());
    }
}
