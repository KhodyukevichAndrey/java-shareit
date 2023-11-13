package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.util.List;

public interface ItemService {

    ItemDto addItem(long ownerId, ItemDto itemDto);

    ItemDto updateItem(long ownerId, long itemId, ItemDto itemDto);

    ItemDto getItemDto(long userId, long itemId);

    List<ItemResponseDto> getAllOwnersItems(long ownerId);

    void deleteItem(long ownerId, long itemId);

    List<ItemDto> searchItems(String text);

    CommentResponseDto addComment(CommentRequestDto commentRequestDto, long userId, long itemId);
}
