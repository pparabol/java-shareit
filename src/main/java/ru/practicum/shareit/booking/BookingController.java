package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDtoResponse saveNewBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @Valid @RequestBody BookingDtoRequest bookingDto) {
        log.info("Запрос на добавление бронирования: userId = {}, bookingDto = {}", userId, bookingDto);
        return bookingService.saveBooking(userId, bookingDto);
    }

    @PatchMapping("{bookingId}")
    public BookingDtoResponse approveBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable long bookingId,
                                             @RequestParam(value = "approved") boolean approved) {
        log.info("Запрос на обновление статуса бронирования: userId = {}, bookingId = {}, approved = {}",
                userId, bookingId, approved
        );
        return bookingService.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("{bookingId}")
    public BookingDtoResponse getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @PathVariable long bookingId) {
        log.info("Запрос на получение бронирования: userId = {}, bookingId = {}", userId, bookingId);
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingDtoResponse> getBookings(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @RequestParam(value = "state",
                                                        defaultValue = "ALL") String state) {
        log.info("Запрос на получение бронирований пользователя: userId = {}, state = {}", userId, state);
        return bookingService.getBookerBookings(userId, state);
    }

    @GetMapping("owner")
    public List<BookingDtoResponse> getBookingsByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                       @RequestParam(value = "state",
                                                               defaultValue = "ALL") String state) {
        log.info("Запрос на получение забронированных вещей владельца: userId = {}, state = {}", userId, state);
        return bookingService.getOwnerBookings(userId, state);
    }
}
