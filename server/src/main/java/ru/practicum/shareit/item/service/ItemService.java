package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.util.List;

public interface ItemService {

    ItemDto addItem(long ownerId, ItemDto itemDto);

    ItemDto updateItem(long ownerId, long itemId, ItemDto itemDto);

    ItemResponseDto getItemDto(long userId, long itemId);

    List<ItemResponseDto> getAllOwnersItems(long ownerId, int from, int size);

    void deleteItem(long ownerId, long itemId);

    List<ItemDto> searchItems(String text, int from, int size);

    CommentResponseDto addComment(CommentRequestDto commentRequestDto, long userId, long itemId);
}
