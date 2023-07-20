package ru.practicum.shareit.item.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> getItems(long userId, PageRequest pageRequest);

    ItemDto getItem(long userId, long id);

    ItemDto saveItem(long userId, ItemDto itemDto);

    ItemDto updateItem(long userId, ItemDto itemDto);

    List<ItemDto> searchItems(long userId, String text, PageRequest pageRequest);

    CommentDto saveComment(long userId, long itemId, CommentDto commentDto);
}
