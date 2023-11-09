package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto addItem(long ownerId, ItemDto itemDto);

    ItemDto updateItem(long ownerId, long itemId, ItemDto itemDto);

    ItemDto getItemDto(long itemId);

    List<ItemDto> getAllOwnersItems(long ownerId);

    void deleteItem(long ownerId, long itemId);

    List<ItemDto> searchItems(String text);
}
