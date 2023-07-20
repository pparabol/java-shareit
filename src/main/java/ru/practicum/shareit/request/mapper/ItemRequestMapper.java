package ru.practicum.shareit.request.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.util.Mapper;

@Component
public class ItemRequestMapper implements Mapper<ItemRequest, ItemRequestDto> {
    @Override
    public ItemRequest toEntity(ItemRequestDto dto) {
        return new ItemRequest(
                dto.getDescription()
        );
    }

    @Override
    public ItemRequestDto toDto(ItemRequest entity) {
        return ItemRequestDto.builder()
                .id(entity.getId())
                .description(entity.getDescription())
                .created(entity.getCreated())
                .build();
    }
}
