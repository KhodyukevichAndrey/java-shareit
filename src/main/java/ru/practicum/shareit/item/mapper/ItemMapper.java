package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.RequestItem;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@UtilityClass
public class ItemMapper {

    public ItemDto makeItemDto(Item item) {
        RequestItem requestItem = item.getRequestItem();
        Long requestId = null;
        if (requestItem != null) {
            requestId = requestItem.getId();
        }
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                requestId
        );
    }

    public Item makeItem(ItemDto itemDto, User user, RequestItem requestItem) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                user,
                requestItem
        );
    }

    public ItemShortDto makeItemShortDto(Item item) {
        return new ItemShortDto(
                item.getId(),
                item.getName()
        );
    }

    public ItemResponseDto makeItemForOwnerDto(ItemDto itemDto, BookingShortDto lastBooking, BookingShortDto nextBooking, List<CommentResponseDto> comments) {
        return new ItemResponseDto(
                itemDto,
                lastBooking,
                nextBooking,
                comments
        );
    }

    public ItemForRequestDto makeItemForRequestDto(Item item) {
        return new ItemForRequestDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                item.getRequestItem().getId()
        );
    }
}
