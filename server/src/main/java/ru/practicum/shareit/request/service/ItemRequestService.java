package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto saveItemRequest(long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getAllByRequestor(long requestorId);

    List<ItemRequestDto> getAll(long userId, int from, int size);

    ItemRequestDto getItemRequest(long userId, long requestId);
}
