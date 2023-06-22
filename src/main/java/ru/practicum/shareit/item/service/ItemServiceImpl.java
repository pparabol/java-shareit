package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public List<ItemDto> getItems(long userId) {
        userRepository.find(userId);
        return itemRepository.findAllByUserId(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItem(long userId, long id) {
        userRepository.find(userId);
        return ItemMapper.toItemDto(itemRepository.find(id));
    }

    @Override
    public ItemDto saveItem(long userId, ItemDto itemDto) {
        User user = userRepository.find(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(user);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto updateItem(long userId, ItemDto itemDto) {
        Item item = itemRepository.find(itemDto.getId());
        if (userId != item.getOwner().getId()) {
            throw new ValidationException(HttpStatus.FORBIDDEN, "Редактировать вещь может только её владелец");
        }
        if (!StringUtils.isBlank(itemDto.getName())) {
            item.setName(itemDto.getName());
        }
        if (!StringUtils.isBlank(itemDto.getDescription())) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return ItemMapper.toItemDto(itemRepository.update(item));
    }

    @Override
    public List<ItemDto> searchItems(long userId, String text) {
        if (StringUtils.isBlank(text)) {
            return Collections.emptyList();
        }
        return itemRepository.search(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
