package ru.practicum.shareit.request;

import jakarta.validation.constraints.Min;
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
        // Для каждого запроса должны быть указаны описание, дата и время создания,
        // а также список ответов в формате: id вещи, название, id владельца.
        // В дальнейшем, используя указанные id вещей, можно будет получить подробную
        // информацию о каждой из них. Запросы должны возвращаться отсортированными от более новых к более старым.
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAll(@RequestParam(name = "from") @Min(0) int from,
                                       @RequestParam(name = "size") @Min(1) int size) {
        log.info("GET /requests/all?from={}&size={} request:", from, size);
        List<ItemRequestDto> requests = requestService.getAll(from, size);
        log.info("GET /requests/all?from={}&size={} response: size={}", from, size, requests.size());
        return requests;
        // С помощью этого эндпоинта пользователи смогут просматривать существующие запросы,
        // на которые они могли бы ответить. Запросы сортируются по дате создания от более новых к более старым.
        // Результаты должны возвращаться постранично.
        // Для этого нужно передать два параметра: from — индекс первого элемента,
        // начиная с 0; и size — количество элементов для отображения.
    }

    @GetMapping("/{requestId}")
    public ItemRequestWithResponsesDto getById(@PathVariable long requestId) {
        log.info("GET /requests/{} request", requestId);
        ItemRequestWithResponsesDto itemRequest = requestService.getById(requestId);
        log.info("GET /requests/{} response: {}", requestId, itemRequest);
        return itemRequest;
        // получить данные об одном конкретном запросе вместе с данными об ответах на него
        // в том же формате, что и в эндпоинте GET /requests
    }
}
