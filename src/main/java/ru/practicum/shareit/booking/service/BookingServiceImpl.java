package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional
    public BookingDtoResponse saveBooking(long userId, BookingDtoRequest bookingDto) {
        User user = findUserOrThrowException(userId);
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(
                () -> new ValidationException(
                        HttpStatus.NOT_FOUND,
                        String.format("Вещь с ID %d не найдена", bookingDto.getItemId()))
        );
        if (!item.getAvailable()) {
            throw new ValidationException(
                    HttpStatus.BAD_REQUEST,
                    String.format("Вещь с ID %d недоступна для аренды", item.getId())
            );
        }
        if (!bookingDto.getStart().isBefore(bookingDto.getEnd())) {
            throw new ValidationException(
                    HttpStatus.BAD_REQUEST,
                    "Время бронирования указано некорректно"
            );
        }
        Booking booking = bookingMapper.toNewEntity(bookingDto, item, user);
        if (userId == booking.getItem().getOwner().getId()) {
            throw new ValidationException(
                    HttpStatus.NOT_FOUND,
                    "Вещь недоступна для бронирования владельцем"
            );
        }
        return bookingMapper.toDtoResponse(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDtoResponse approveBooking(long userId, long bookingId, boolean approved) {
        Booking booking = findBookingOrThrowException(bookingId);
        if (userId != booking.getItem().getOwner().getId()) {
            throw new ValidationException(
                    HttpStatus.NOT_FOUND,
                    "Изменять статус бронирования может только владелец вещи"
            );
        }
        if (booking.getStatus().equals(Status.APPROVED)) {
            throw new ValidationException(
                    HttpStatus.BAD_REQUEST,
                    String.format("Бронирование с ID %d уже подтверждено", bookingId)
            );
        }
        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        bookingRepository.save(booking);
        return bookingMapper.toDtoResponse(booking);
    }

    @Override
    public BookingDtoResponse getBooking(long userId, long bookingId) {
        Booking booking = findBookingOrThrowException(bookingId);
        if (booking.getBooker().getId() == userId ||
                booking.getItem().getOwner().getId() == userId) {
            return bookingMapper.toDtoResponse(booking);
        } else {
            throw new ValidationException(
                    HttpStatus.NOT_FOUND,
                    String.format("Информация о бронировании с ID %d недоступна для просмотра",
                            bookingId)
            );
        }
    }

    @Override
    @Transactional
    public List<BookingDtoResponse> getBookerBookings(long userId, String state) {
        findUserOrThrowException(userId);
        List<Booking> bookings = new ArrayList<>();
        State stateValue = getStateOrThrowException(state);
        switch (stateValue) {
            case ALL:
                bookings = bookingRepository.findByBookerIdOrderByStartDesc(userId);
                break;
            case CURRENT:
                bookings = bookingRepository.findBookerCurrentBookings(userId);
                break;
            case PAST:
                bookings = bookingRepository.findBookerPastBookings(userId);
                break;
            case FUTURE:
                bookings = bookingRepository.findBookerFutureBookings(userId);
                break;
            case WAITING:
            case REJECTED:
                bookings = bookingRepository.findByBookerIdAndStatus(
                        userId,
                        Status.valueOf(state)
                );
                break;
        }
        return bookings.isEmpty() ? Collections.emptyList() : bookings.stream()
                .map(bookingMapper::toDtoResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<BookingDtoResponse> getOwnerBookings(long userId, String state) {
        findUserOrThrowException(userId);
        List<Booking> bookings = new ArrayList<>();
        State stateValue = getStateOrThrowException(state);
        switch (stateValue) {
            case ALL:
                bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(userId);
                break;
            case CURRENT:
                bookings = bookingRepository.findOwnerCurrentBookings(userId);
                break;
            case PAST:
                bookings = bookingRepository.findOwnerPastBookings(userId);
                break;
            case FUTURE:
                bookings = bookingRepository.findOwnerFutureBookings(userId);
                break;
            case WAITING:
            case REJECTED:
                bookings = bookingRepository.findByItemOwnerIdAndStatus(
                        userId,
                        Status.valueOf(state)
                );
                break;
        }
        return bookings.isEmpty() ? Collections.emptyList() : bookings.stream()
                .map(bookingMapper::toDtoResponse)
                .collect(Collectors.toList());
    }

    private Booking findBookingOrThrowException(long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(
                () -> new ValidationException(
                        HttpStatus.NOT_FOUND,
                        String.format("Бронирование с ID %d не найдено", bookingId))
        );
    }

    private User findUserOrThrowException(long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new ValidationException(
                        HttpStatus.NOT_FOUND,
                        String.format("Пользователь с ID %d не найден", userId))
        );
    }

    private State getStateOrThrowException(String state) {
        try {
            return State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new ValidationException(
                    HttpStatus.BAD_REQUEST,
                    String.format("Unknown state: %s", state)
            );
        }
    }
}
