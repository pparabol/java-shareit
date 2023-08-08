package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

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
                                         @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.saveItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getAllByRequestor(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestService.getAllByRequestor(userId);
    }

    @GetMapping("all")
    public List<ItemRequestDto> getAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                       @RequestParam(defaultValue = "0") int from,
                                       @RequestParam(defaultValue = "10") int size) {
        return itemRequestService.getAll(
                userId,
                PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "created"))
        );
    }

    @GetMapping("{requestId}")
    public ItemRequestDto getItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @PathVariable long requestId) {
        return itemRequestService.getItemRequest(userId, requestId);
    }
}
