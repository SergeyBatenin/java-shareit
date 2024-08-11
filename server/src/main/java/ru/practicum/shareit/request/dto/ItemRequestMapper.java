package ru.practicum.shareit.request.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ItemRequestMapper {
    public ItemRequest dtoToItemRequest(ItemRequestCreateDto requestDto, User user) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(requestDto.getDescription());
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());
        return itemRequest;
    }

    public ItemRequestDto requestToDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getRequestor().getId(),
                itemRequest.getCreated()
        );
    }

    public ItemRequestWithResponsesDto requestToDtoWithResponses(ItemRequest itemRequest,
                                                                 List<Item> items) {
        List<ItemRequestResponse> responses = items.stream()
                .map(item -> new ItemRequestResponse(item.getId(), item.getName(), item.getOwner().getId()))
                .collect(Collectors.toList());
        return new ItemRequestWithResponsesDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getRequestor().getId(),
                itemRequest.getCreated(),
                responses
        );
    }
}
