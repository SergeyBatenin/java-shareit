package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;

import java.util.Collection;

public interface ItemService {
    ItemDto create(ItemDto itemDto, long ownerId);

    ItemDto update(ItemDto itemDto, long ownerId);

    ItemInfoDto getById(long itemId, long userId);

    Collection<ItemInfoDto> getByOwner(long ownerId);

    Collection<ItemDto> search(String text);

    CommentDto addComment(CommentDto commentDto, Long authorId);
}
