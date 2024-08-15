package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

import java.util.Collections;

import static ru.practicum.shareit.constants.UserIdHttpHeader.USER_ID_HEADER;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(USER_ID_HEADER) long userId,
                                             @RequestBody @Validated(Create.class) ItemDto itemDto) {
        log.info("Creating item {}, userId={}", itemDto, userId);
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@Validated(Update.class) @RequestBody ItemDto itemDto,
                                             @PathVariable long itemId,
                                             @RequestHeader(USER_ID_HEADER) long userId) {
        log.info("Update item {}, itemId={} userId={}", itemDto, itemId, userId);
        return itemClient.updateItem(itemDto, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable long itemId, @RequestHeader(USER_ID_HEADER) long userId) {
        log.info("Get item by itemId={}, userId={}", itemId, userId);
        return itemClient.getItemById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemByOwner(@RequestHeader(USER_ID_HEADER) long userId) {
        log.info("Get items by owner, userId={}", userId);
        return itemClient.getItemByOwner(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestHeader(USER_ID_HEADER) long userId,
                                              @RequestParam(name = "text") String text) {
        log.info("Search items by text={}", text);
        return itemClient.searchItems(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@Valid @RequestBody CommentCreateDto commentDto,
                                             @PathVariable @Positive long itemId,
                                             @RequestHeader(USER_ID_HEADER) long userId) {
        log.info("Add comment {} to item with id={}, userId{}", commentDto, itemId, userId);
        return itemClient.addComment(commentDto, itemId, userId);
    }
}
