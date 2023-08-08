package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                           @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Get items userId={}, from={}, size={}", userId, from, size);
        return itemClient.getItems(userId, from, size);
    }

    @GetMapping("{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @PathVariable Long itemId) {
        log.info("Get item itemId={}, userId={}", itemId, userId);
        return itemClient.getItem(userId, itemId);
    }

    @PostMapping
    public ResponseEntity<Object> saveNewItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @Valid @RequestBody ItemDto itemDto) {
        log.info("Creating item {}, userId={}", itemDto, userId);
        return itemClient.saveNewItem(userId, itemDto);
    }

    @PatchMapping("{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable Long itemId,
                                             @RequestBody ItemDto itemDto) {
        log.info("Updating item {}, userId={}", itemDto, userId);
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @RequestParam String text,
                                              @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                              @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Searching items by criteria '{}', userId={}, from={}, size={}",
                text, userId, from, size
        );
        return itemClient.searchItems(userId, text, from, size);
    }

    @PostMapping("{itemId}/comment")
    public ResponseEntity<Object> saveNewComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @PathVariable long itemId,
                                                 @Valid @RequestBody CommentDto commentDto) {
        log.info(" Commenting on item itemId={}, text={}, userId={}",
                itemId, commentDto.getText(), userId
        );
        return itemClient.commentOnItem(userId, itemId, commentDto);
    }
}
