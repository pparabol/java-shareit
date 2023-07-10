package ru.practicum.shareit.booking.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.util.Mapper;

@Component
@RequiredArgsConstructor
public class BookingMapper {
    private final Mapper<User, UserDto> userMapper;
    private final Mapper<Item, ItemDto> itemMapper;

    public Booking toNewEntity(BookingDtoRequest bookingDtoRequest, Item item, User user) {
        return new Booking(
                bookingDtoRequest.getId(),
                bookingDtoRequest.getStart(),
                bookingDtoRequest.getEnd(),
                item,
                user,
                Status.WAITING
        );
    }

    public BookingDtoResponse toDtoResponse(Booking booking) {
        return new BookingDtoResponse(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                itemMapper.toDto(booking.getItem()),
                userMapper.toDto(booking.getBooker()),
                booking.getStatus()
        );
    }

    public BookingDtoShort toDtoShort(Booking booking) {
        return new BookingDtoShort(
                booking.getId(),
                booking.getBooker().getId(),
                booking.getStart(),
                booking.getEnd()
        );
    }
}
