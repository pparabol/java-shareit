package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Запрос на получение всех вещей пользователя {}", userId);
        return itemService.getItems(userId);
    }

    @GetMapping("{id}")
    public ItemDto getItem(@RequestHeader("X-Sharer-User-Id") long userId,
                           @PathVariable long id) {
        log.info("Запрос на получение вещи: itemId = {}, userId = {}", id, userId);
        return itemService.getItem(userId, id);
    }

    @PostMapping
    public ItemDto saveNewItem(@RequestHeader("X-Sharer-User-Id") long userId,
                               @Valid @RequestBody ItemDto itemDto) {
        log.info("Запрос на добавление вещи: userId = {}, itemDto = {}", userId, itemDto);
        return itemService.saveItem(userId, itemDto);
    }

    @PatchMapping("{id}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                              @PathVariable long id,
                              @RequestBody ItemDto itemDto) {
        itemDto.setId(id);
        log.info("Запрос на редактирование вещи: userId = {}, itemDto = {}", userId, itemDto);
        return itemService.updateItem(userId, itemDto);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestHeader("X-Sharer-User-Id") long userId,
                                     @RequestParam(value = "text") String text) {
        log.info("Запрос на поиск вещей по критерию '{}', userId = {}", text, userId);
        return itemService.searchItems(userId, text);
    }
}
