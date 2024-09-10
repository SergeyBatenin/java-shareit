package ru.practicum.shareit.request;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

import static ru.practicum.shareit.constants.UserIdHttpHeader.USER_ID_HEADER;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@Validated @RequestBody ItemRequestCreateDto requestDto,
                                                @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Creating item request {}, userId={}", requestDto, userId);
        return itemRequestClient.createItemRequest(requestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllRequestsByUser(@RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Get all item requests by user with id={}", userId);
        return itemRequestClient.getAllRequestsByUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestParam(name = "from") @PositiveOrZero int from,
                                                @RequestParam(name = "size") @Positive int size) {
        log.info("Get all item requests, page from={}, size={}", from, size);
        return itemRequestClient.getAllRequests(from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@PathVariable long requestId) {
        log.info("Get item request by id={}", requestId);
        return itemRequestClient.getRequestById(requestId);
    }
}
