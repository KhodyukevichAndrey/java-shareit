package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import java.util.Collections;

import static ru.practicum.shareit.constants.headers.Headers.USER_ID;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader(USER_ID) long userId,
                                          @Validated(ItemDto.Create.class) @RequestBody ItemDto itemDto) {
        log.debug("Получен запрос POST /items");
        return itemClient.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(USER_ID) long userId,
                                             @PathVariable long itemId,
                                             @Validated(ItemDto.Update.class) @RequestBody ItemDto itemDto) {
        log.debug("Получен запрос PUT /items");
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader(USER_ID) long userId, @PathVariable long itemId) {
        log.debug("Получен запрос GET /items/{itemId}");
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllOwnersItem(@RequestHeader(USER_ID) long userId,
                                                   @RequestParam(defaultValue = "0") @Min(0) @Max(50)
                                                   int from,
                                                   @RequestParam(defaultValue = "20") @Min(1) @Max(50)
                                                   int size) {
        log.debug("Получен запрос GET /items");
        return itemClient.getAllOwnersItem(userId, from, size);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteItem(@RequestHeader(USER_ID) long userId, @PathVariable long itemId) {
        log.debug("Получен запрос DELETE /items/{itemId}");
        return itemClient.deleteItem(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text,
                                              @RequestParam(defaultValue = "0") @Min(0) @Max(50) int from,
                                              @RequestParam(defaultValue = "20") @Min(1) @Max(50) int size) {
        log.debug("Получен запрос GET /items/search");
        if (text.isBlank()) {
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
        }
        return itemClient.searchItems(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestBody @Valid CommentRequestDto comment,
                                             @RequestHeader(USER_ID) long userId,
                                             @PathVariable long itemId) {
        log.debug("Получен запрос POST /items/{itemId}/comment");
        return itemClient.addComment(comment, userId, itemId);
    }
}