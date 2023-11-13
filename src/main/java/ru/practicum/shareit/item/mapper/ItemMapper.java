package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Component
public class ItemMapper {

    public ItemDto makeItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable());
    }

    public Item makeItem(ItemDto itemDto, User user) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                user);
    }

    public ItemShortDto makeItemShortDto(Item item) {
        return new ItemShortDto(
                item.getId(),
                item.getName()
        );
    }

    public ItemResponseDto makeItemForOwnerDto(Item item, BookingShortDto lastBooking, BookingShortDto nextBooking, List<CommentResponseDto> comments) {
        return new ItemResponseDto(
                item,
                lastBooking,
                nextBooking,
                comments
                );
    }
}
