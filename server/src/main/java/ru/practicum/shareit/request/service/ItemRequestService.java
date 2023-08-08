package ru.practicum.shareit.request.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto saveItemRequest(long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getAllByRequestor(long requestorId);

    List<ItemRequestDto> getAll(long userId, PageRequest pageRequest);

    ItemRequestDto getItemRequest(long userId, long requestId);
}
