package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnauthorizedModificationItem;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto create(Item item) {
        long ownerId = item.getOwner().getId();
        userRepository.getById(ownerId)
                .orElseThrow(() -> {
                    log.debug("CREATE ITEM. Пользователь с айди {} не найден", ownerId);
                    return new NotFoundException("Пользователь с id=" + ownerId + " не существует");
                });
        Item createdItem = itemRepository.create(item);
        return ItemMapper.itemToDTO(createdItem);
    }

    @Override
    public ItemDto update(Item item, long itemId, long userId) {
        Item updatedItem = itemRepository.getById(itemId)
                .orElseThrow(() -> {
                    log.debug("UPDATE ITEM By ID={}. Вещь с айди {} не найден", itemId, itemId);
                    return new NotFoundException("Вещь с id=" + itemId + " не существует");
                });
        if (updatedItem.getOwner().getId() != userId) {
            throw new UnauthorizedModificationItem("Менять описание вещи может только владелец");
        }
        item.setId(itemId);
        return ItemMapper.itemToDTO(itemRepository.update(item));
    }

    @Override
    public ItemDto getById(long itemId) {
        Item item = itemRepository.getById(itemId)
                .orElseThrow(() -> {
                    log.debug("UPDATE ITEM By ID={}. Вещь с айди {} не найден", itemId, itemId);
                    return new NotFoundException("Вещь с id=" + itemId + " не существует");
                });

        return ItemMapper.itemToDTO(item);
    }

    @Override
    public Collection<ItemDto> getByOwner(long userId) {
        userRepository.getById(userId)
                .orElseThrow(() -> {
                    log.debug("GET ITEMS BY OWNER. Пользователь с айди {} не найден", userId);
                    return new NotFoundException("Пользователь с id=" + userId + " не существует");
                });

        return itemRepository.getByOwner(userId);
    }

    @Override
    public Collection<ItemDto> search(String text) {
        return itemRepository.search(text.toLowerCase());
    }
}
