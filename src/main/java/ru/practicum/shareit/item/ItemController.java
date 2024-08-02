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
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
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
        ItemDto createdItem = itemService.create(itemDto, userId);
        log.info("POST /items response: {}", createdItem);
        return createdItem;
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestBody ItemDto itemDto,
                          @PathVariable long itemId,
                          @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("PATCH /items/{}, userId={} request: {}", itemId, userId, itemDto);
        itemDto.setId(itemId);
        ItemDto updatedItem = itemService.update(itemDto, userId);
        log.info("PATCH /items/{}, userId={} response: {}", itemId, userId, itemDto);
        return updatedItem;
    }

    @GetMapping("/{itemId}")
    public ItemInfoDto getById(@PathVariable long itemId, @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("GET /items/{} request", itemId);
        ItemInfoDto itemDto = itemService.getById(itemId, userId);
        log.info("GET /items/{} response: {}", itemId, itemDto);
        return itemDto;
    }

    @GetMapping
    public Collection<ItemInfoDto> getByOwner(@RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("GET /items, ownerId={} request", userId);
        Collection<ItemInfoDto> itemsDto = itemService.getByOwner(userId);
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

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestBody CommentDto commentDto,
                                 @PathVariable long itemId,
                                 @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("ADD COMMENT /items/{}/comment={} request", itemId, commentDto);
        commentDto.setItemId(itemId);
        CommentDto createdComment = itemService.addComment(commentDto, userId);
        log.info("ADD COMMENT /items/{}/comment={} response", itemId, commentDto);
        return createdComment;
    }
}
