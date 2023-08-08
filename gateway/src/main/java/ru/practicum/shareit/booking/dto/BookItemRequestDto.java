package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookItemRequestDto {
    private long itemId;

    @NotNull(message = "Необходимо указать дату начала аренды")
    @FutureOrPresent(message = "Дата начала аренды не может быть в прошлом")
    private LocalDateTime start;

    @NotNull(message = "Необходимо указать дату окончания аренды")
    @Future(message = "Дата окончания аренды должна быть в будущем")
    private LocalDateTime end;
}
