package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    List<Item> findAllByUserId(long userId);
    Item find(long id);
    Item save(Item item);
    Item update(Item item);
    List<Item> search(String text);
}
