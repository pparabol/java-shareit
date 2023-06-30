package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    List<Item> findAllByUserId(long userId);

    Optional<Item> find(long id);

    Item save(Item item);

    Item update(Item item);

    List<Item> search(String text);
}
