package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@Slf4j
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto saveNewRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Запрос на добавление запроса вещи: userId = {}, description = {}",
                userId, itemRequestDto.getDescription()
        );
        return itemRequestService.saveItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getAllByRequestor(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Запрос на получение запросов пользователя userId = {}", userId);
        return itemRequestService.getAllByRequestor(userId);
    }

    @GetMapping("all")
    public List<ItemRequestDto> getAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                       @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                       @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Запрос на получение актуальных запросов вещей: " +
                        "userId = {}, from = {}, size = {}",
                userId, from, size
        );
        return itemRequestService.getAll(
                userId,
                PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "created"))
        );
    }

    @GetMapping("{requestId}")
    public ItemRequestDto getItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @PathVariable long requestId) {
        log.info("Запрос на получение запроса вещи: userId = {}, requestId = {}", userId, requestId);
        return itemRequestService.getItemRequest(userId, requestId);
    }
}
