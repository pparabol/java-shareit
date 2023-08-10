package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> getItems(long userId, int from, int size);

    ItemDto getItem(long userId, long id);

    ItemDto saveItem(long userId, ItemDto itemDto);

    ItemDto updateItem(long userId, ItemDto itemDto);

    List<ItemDto> searchItems(long userId, String text, int from, int size);

    CommentDto saveComment(long userId, long itemId, CommentDto commentDto);
}
