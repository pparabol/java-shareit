package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;


@Builder
@Data
public class ItemRequestDto {
    private Long id;
    @NotBlank(message = "Описание не может быть пустым")
    private String description;
    private LocalDateTime created;
    private List<ItemDto> items;
}
