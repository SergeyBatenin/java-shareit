package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

import static ru.practicum.shareit.constants.UserIdHttpHeader.USER_ID_HEADER;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@Validated @RequestBody ItemDto itemDto,
                          @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("POST /items request: {}", itemDto);
        Item item = ItemMapper.dtoToItem(itemDto);
        item.setOwnerId(userId);
        ItemDto createdItem = itemService.create(item);
        log.info("POST /items response: {}", createdItem);
        return createdItem;
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestBody ItemDto itemDto,
                          @PathVariable long itemId,
                          @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("PATCH /items/{}, userId={} request: {}", itemId, userId, itemDto);
        Item item = ItemMapper.dtoToItem(itemDto);
        ItemDto updatedItem = itemService.update(item, itemId, userId);
        log.info("PATCH /items/{}, userId={} response: {}", itemId, userId, itemDto);
        return updatedItem;
    }

    @GetMapping("/{itemId}")
    public ItemDto getById(@PathVariable long itemId) {
        log.info("GET /items/{} request", itemId);
        ItemDto itemDto = itemService.getById(itemId);
        log.info("GET /items/{} response: {}", itemId, itemDto);
        return itemDto;
    }

    @GetMapping
    public Collection<ItemDto> getByOwner(@RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("GET /items, ownerId={} request", userId);
        Collection<ItemDto> itemsDto = itemService.getByOwner(userId);
        log.info("GET /items, ownerId={} response: {}", userId, itemsDto.size());
        return itemsDto;
    }

    @GetMapping("/search")
    public Collection<ItemDto> search(@RequestParam(name = "text") String text) {
        log.info("SEARCH /items/search/{} request", text);
        Collection<ItemDto> itemsDto = itemService.search(text);
        log.info("SEARCH /items/search/{} response: {}", text, itemsDto.size());
        return itemsDto;
    }
}
