package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithResponsesDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static ru.practicum.shareit.constants.UserIdHttpHeader.USER_ID_HEADER;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
@Slf4j
public class ItemRequestController {
    private final ItemRequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDto createRequest(@Validated @RequestBody ItemRequestCreateDto requestDto, @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("POST /requests request: {}", requestDto);
        ItemRequestDto createdRequest = requestService.create(requestDto, userId);
        log.info("POST /requests response: {}", createdRequest);
        return createdRequest;
    }

    @GetMapping
    public List<ItemRequestWithResponsesDto> getAllByUser(@RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("GET /requests request: userId={}", userId);
        List<ItemRequestWithResponsesDto> requests = requestService.getAllByUser(userId);
        log.info("GET /requests response: userId={}, size={}", userId, requests.size());
        return requests;
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAll(@RequestParam(name = "from") int from,
                                       @RequestParam(name = "size") int size) {
        log.info("GET /requests/all?from={}&size={} request:", from, size);
        List<ItemRequestDto> requests = requestService.getAll(from, size);
        log.info("GET /requests/all?from={}&size={} response: size={}", from, size, requests.size());
        return requests;
    }

    @GetMapping("/{requestId}")
    public ItemRequestWithResponsesDto getById(@PathVariable long requestId) {
        log.info("GET /requests/{} request", requestId);
        ItemRequestWithResponsesDto itemRequest = requestService.getById(requestId);
        log.info("GET /requests/{} response: {}", requestId, itemRequest);
        return itemRequest;
    }
}
