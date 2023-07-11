package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Value
@Builder
public class BookingDto {
    Long id;
    LocalDateTime start;
    LocalDateTime end;
    ItemDto item;
    UserDto booker;
    Status status;
}
