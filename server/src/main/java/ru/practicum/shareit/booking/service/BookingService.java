package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;

import java.util.List;

public interface BookingService {
    BookingDto saveBooking(long userId, BookingDtoRequest bookingDto);

    BookingDto approveBooking(long userId, long bookingId, boolean approved);

    BookingDto getBooking(long userId, long bookingId);

    List<BookingDto> getBookerBookings(long userId, String state, int from, int size);

    List<BookingDto> getOwnerBookings(long userId, String state, int from, int size);
}
