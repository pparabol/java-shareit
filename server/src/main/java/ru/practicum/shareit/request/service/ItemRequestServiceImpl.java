package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final Mapper<ItemRequest, ItemRequestDto> itemRequestMapper;
    private final Mapper<Item, ItemDto> itemMapper;

    @Override
    @Transactional
    public ItemRequestDto saveItemRequest(long userId, ItemRequestDto itemRequestDto) {
        User user = findUserOrThrowException(userId);
        ItemRequest request = itemRequestMapper.toEntity(itemRequestDto);
        request.setRequestor(user);
        return itemRequestMapper.toDto(itemRequestRepository.save(request));
    }

    @Override
    public List<ItemRequestDto> getAllByRequestor(long requestorId) {
        findUserOrThrowException(requestorId);
        return itemRequestRepository.findByRequestorIdOrderByCreatedDesc(requestorId).stream()
                .map(itemRequestMapper::toDto)
                .map(this::uploadItems)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAll(long userId, int from, int size) {
        findUserOrThrowException(userId);
        PageRequest pageRequest = PageRequest.of(
                from / size,
                size,
                Sort.by(Sort.Direction.DESC, "created")
        );
        return itemRequestRepository.findAllByRequestorIdNot(userId, pageRequest).stream()
                .map(itemRequestMapper::toDto)
                .map(this::uploadItems)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getItemRequest(long userId, long requestId) {
        findUserOrThrowException(userId);
        return itemRequestRepository.findById(requestId)
                .map(itemRequestMapper::toDto)
                .map(this::uploadItems)
                .orElseThrow(
                        () -> new NotFoundException(
                                String.format("Запрос с ID %d не найден", requestId)
                        )
                );
    }

    private User findUserOrThrowException(long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(
                        String.format("Пользователь с ID %d не найден", userId))
        );
    }

    private ItemRequestDto uploadItems(ItemRequestDto itemRequestDto) {
        List<ItemDto> items = itemRepository.findByRequestId(itemRequestDto.getId()).stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
        itemRequestDto.setItems(items);
        return itemRequestDto;
    }
}
