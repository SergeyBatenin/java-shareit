package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {
    ItemDto create(Item item);

    ItemDto update(Item item, long itemId, long userId);

    ItemDto getById(long itemId);

    Collection<ItemDto> getByOwner(long userId);

    Collection<ItemDto> search(String text);
}
