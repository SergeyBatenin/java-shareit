package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemRepository implements ItemRepository {
    private static long identifier = 1;
    private final Map<Long, Item> items = new HashMap<>();
    private final Map<Long, List<Item>> userItemIndex = new LinkedHashMap<>();

    @Override
    public Item create(Item item) {
        item.setId(identifier);
        items.put(identifier, item);
        identifier++;

        final List<Item> userItems = userItemIndex.computeIfAbsent(item.getOwnerId(), k -> new ArrayList<>());
        userItems.add(item);

        return item;
    }

    @Override
    public Item update(Item item) {
        long itemId = item.getId();
        Item updatedItem = items.get(itemId);

        if (item.getName() != null && !item.getName().isBlank()) {
            updatedItem.setName(item.getName());
        }
        if (item.getDescription() != null && !item.getDescription().isBlank()) {
            updatedItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            updatedItem.setAvailable(item.getAvailable());
        }

        return updatedItem;
    }

    @Override
    public Optional<Item> getById(long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public Collection<ItemDto> getByOwner(long userId) {
        return userItemIndex.get(userId)
                .stream().map(ItemMapper::itemToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> search(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }

        return items.values().stream()
                .filter(item -> (item.getName().toLowerCase().contains(text)
                        || item.getDescription().toLowerCase().contains(text))
                        && item.getAvailable())
                .map(ItemMapper::itemToDTO)
                .collect(Collectors.toList());
    }
}
