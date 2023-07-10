package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;

import java.util.List;

public interface BookingService {
    BookingDtoResponse saveBooking(long userId, BookingDtoRequest bookingDto);

    BookingDtoResponse approveBooking(long userId, long bookingId, boolean approved);

    BookingDtoResponse getBooking(long userId, long bookingId);

    List<BookingDtoResponse> getBookerBookings(long userId, String state);

    List<BookingDtoResponse> getOwnerBookings(long userId, String state);
}
