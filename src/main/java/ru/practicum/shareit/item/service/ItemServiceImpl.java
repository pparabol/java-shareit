package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.Mapper;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final Mapper<Item, ItemDto> itemMapper;
    private final BookingMapper bookingMapper;
    private final Mapper<Comment, CommentDto> commentMapper;

    @Override
    public List<ItemDto> getItems(long userId) {
        return itemRepository.findByOwnerId(userId).stream()
                .map(itemMapper::toDto)
                .map(this::uploadBookings)
                .map(this::uploadComments)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItem(long userId, long id) {
        Item item = findItemOrThrowException(id);
        ItemDto itemDto = itemMapper.toDto(item);
        if (item.getOwner().getId() == userId) {
            uploadBookings(itemDto);
        }
        return uploadComments(itemDto);
    }

    @Override
    @Transactional
    public ItemDto saveItem(long userId, ItemDto itemDto) {
        User user = findUserOrThrowException(userId);
        Item item = itemMapper.toEntity(itemDto);
        item.setOwner(user);
        return itemMapper.toDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemDto updateItem(long userId, ItemDto itemDto) {
        Item item = findItemOrThrowException(itemDto.getId());
        if (userId != item.getOwner().getId()) {
            throw new NotFoundException("Редактировать вещь может только её владелец");
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
        return itemMapper.toDto(itemRepository.save(item));
    }

    @Override
    public List<ItemDto> searchItems(long userId, String text) {
        if (StringUtils.isBlank(text)) {
            return Collections.emptyList();
        }
        return itemRepository.searchByQuery(text.toLowerCase()).stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto saveComment(long userId, long itemId, CommentDto commentDto) {
        User author = findUserOrThrowException(userId);
        Item item = findItemOrThrowException(itemId);
        bookingRepository.findFirstByItemIdAndBookerIdAndEndBefore(itemId, userId, LocalDateTime.now())
                .orElseThrow(
                        () -> new ValidationException(
                                "Комментировать вещь можно только после завершения её аренды"
                        )
                );
        Comment comment = commentMapper.toEntity(commentDto);
        comment.setAuthor(author);
        comment.setItem(item);
        return commentMapper.toDto(commentRepository.save(comment));
    }

    private Item findItemOrThrowException(long itemId) {
        return itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException(
                        String.format("Вещь с ID %d не найдена", itemId))
        );
    }

    private User findUserOrThrowException(long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(
                        String.format("Пользователь с ID %d не найден", userId))
        );
    }

    private ItemDto uploadBookings(ItemDto itemDto) {
        Booking lastBooking = bookingRepository.findFirstByItemIdAndStartBeforeOrderByStartDesc(
                itemDto.getId(),
                LocalDateTime.now()
        );
        Booking nextBooking = bookingRepository.findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(
                itemDto.getId(),
                LocalDateTime.now(),
                Status.APPROVED
        );
        if (lastBooking != null) {
            itemDto.setLastBooking(bookingMapper.toDtoShort(lastBooking));
        }
        if (nextBooking != null) {
            itemDto.setNextBooking(bookingMapper.toDtoShort(nextBooking));
        }
        return itemDto;
    }

    private ItemDto uploadComments(ItemDto itemDto) {
        List<CommentDto> comments = commentRepository.findByItemId(itemDto.getId()).stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
        itemDto.setComments(comments);
        return itemDto;
    }
}
