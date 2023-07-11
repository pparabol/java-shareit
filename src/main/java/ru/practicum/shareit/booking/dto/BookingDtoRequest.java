package ru.practicum.shareit.booking.dto;

import lombok.Value;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Value
public class BookingDtoRequest {
    @NotNull(message = "Необходимо указать идентификатор вещи")
    Long itemId;

    @NotNull(message = "Необходимо указать дату начала аренды")
    @FutureOrPresent(message = "Дата начала аренды не может быть в прошлом")
    LocalDateTime start;

    @NotNull(message = "Необходимо указать дату окончания аренды")
    @Future(message = "Дата окончания аренды должна быть в будущем")
    LocalDateTime end;
}
