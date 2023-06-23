package ru.practicum.shareit.util;

public interface Mapper<ENTITY, DTO> {
    ENTITY toEntity(DTO dto);

    DTO toDto(ENTITY entity);
}
