package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    ItemDto create(ItemDto itemDto, long ownerId);

    ItemDto update(ItemDto itemDto, long ownerId);

    ItemDto getById(long itemId);

    Collection<ItemDto> getByOwner(long ownerId);

    Collection<ItemDto> search(String text);
}
