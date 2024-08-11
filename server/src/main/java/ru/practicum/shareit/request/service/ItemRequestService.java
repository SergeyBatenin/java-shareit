package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithResponsesDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(ItemRequestCreateDto requestDto, long userId);

    List<ItemRequestWithResponsesDto> getAllByUser(Long userId);

    List<ItemRequestDto> getAll(int from, int size);

    ItemRequestWithResponsesDto getById(long requestId);
}
