package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestWithResponsesDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestMapper requestMapper;

    @Override
    @Transactional
    public ItemRequestDto create(ItemRequestCreateDto requestDto, long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.debug("CREATE ITEMREQUEST. Пользователь с айди {} не найден", userId);
                    return new NotFoundException("Пользователь с id=" + userId + " не существует");
                });
        ItemRequest itemRequest = requestMapper.dtoToItemRequest(requestDto, user);

        return requestMapper.requestToDto(requestRepository.save(itemRequest));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestWithResponsesDto> getAllByUser(Long userId) {
        // Для каждого запроса должны быть указаны описание, дата и время создания,
        // а также список ответов в формате: id вещи, название, id владельца.
        // В дальнейшем, используя указанные id вещей, можно будет получить подробную
        // информацию о каждой из них. Запросы должны возвращаться отсортированными от более новых к более старым.
        userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.debug("GET ITEMREQUEST BY USER. Пользователь с айди {} не найден", userId);
                    return new NotFoundException("Пользователь с id=" + userId + " не существует");
                });

        List<ItemRequest> requests = requestRepository.findAllByRequestorIdOrderByCreatedDesc(userId);
        Map<Long, List<Item>> items = itemRepository.findByRequestIn(requests).stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId()));

        return requests.stream()
                .map(request -> requestMapper.requestToDtoWithResponses(
                        request,
                        items.getOrDefault(request.getId(), new ArrayList<>())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getAll(int from, int size) {
        // С помощью этого эндпоинта пользователи смогут просматривать существующие запросы,
        // на которые они могли бы ответить. Запросы сортируются по дате создания от более новых к более старым.
        // Результаты должны возвращаться постранично.
        // Для этого нужно передать два параметра: from — индекс первого элемента,
        // начиная с 0; и size — количество элементов для отображения.
        Pageable page = PageRequest.of(from > 0 ? from / size : 0, size);
        return requestRepository.findAllByOrderByCreatedDesc(page).stream()
                .map(requestMapper::requestToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestWithResponsesDto getById(long requestId) {
        ItemRequest itemRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> {
                    log.debug("GET BY ID ITEMREQUEST. Запрос с айди {} не найден", requestId);
                    return new NotFoundException("Запрос с id=" + requestId + " не существует");
                });
        List<Item> items = itemRepository.findByRequestId(requestId);
        return requestMapper.requestToDtoWithResponses(itemRequest, items);
    }
}
