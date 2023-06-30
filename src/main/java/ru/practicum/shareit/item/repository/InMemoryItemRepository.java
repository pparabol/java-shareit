package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemRepository implements ItemRepository {
    private final Map<Long, Item> items = new ConcurrentHashMap<>();
    private static long nextId;

    @Override
    public List<Item> findAllByUserId(long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Item> find(long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public Item save(Item item) {
        item.setId(generateId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public List<Item> search(String text) {
        List<Item> result = new ArrayList<>();
        List<Item> availableItems = items.values().stream()
                .filter(item -> item.getAvailable().equals(true))
                .collect(Collectors.toList());
        Pattern pattern = Pattern.compile(text.toLowerCase());
        for (Item item : availableItems) {
            Matcher matcher = pattern.matcher(item.toString().toLowerCase());
            if (matcher.find()) {
                result.add(item);
            }
        }
        return result;
    }

    private long generateId() {
        return ++nextId;
    }
}
