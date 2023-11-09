package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static ru.practicum.shareit.headers.HeadersConstants.OWNER_ID;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto addItem(@RequestHeader(OWNER_ID) long userId,
                           @Validated(ItemDto.Create.class) @RequestBody ItemDto itemDto) {
        log.debug("Получен запрос POST /items");
        return itemService.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(OWNER_ID) long userId,
                              @PathVariable long itemId,
                              @Validated(ItemDto.Update.class) @RequestBody ItemDto itemDto) {
        log.debug("Получен запрос PUT /items");
        return itemService.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable long itemId) {
        log.debug("Получен запрос GET /items/{itemId}");
        return itemService.getItemDto(itemId);
    }

    @GetMapping
    public List<ItemDto> getAllOwnersItem(@RequestHeader(OWNER_ID) long userId) {
        log.debug("Получен запрос GET /items");
        return itemService.getAllOwnersItems(userId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader(OWNER_ID) long userId, @PathVariable long itemId) {
        log.debug("Получен запрос DELETE /items/{itemId}");
        itemService.deleteItem(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        log.debug("Получен запрос GET /items/search");
        return itemService.searchItems(text);
    }
}
