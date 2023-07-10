package ru.practicum.shareit.booking.dto;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class BookingDtoShort {
    Long id;
    Long bookerId;
    LocalDateTime start;
    LocalDateTime end;
}
