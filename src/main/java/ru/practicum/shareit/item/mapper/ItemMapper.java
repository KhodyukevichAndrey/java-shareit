package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.RequestItem;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Component
public class ItemMapper {

    public static ItemDto makeItemDto(Item item) {
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

    public static Item makeItem(ItemDto itemDto, User user, RequestItem requestItem) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                user,
                requestItem
        );
    }

    public static ItemShortDto makeItemShortDto(Item item) {
        return new ItemShortDto(
                item.getId(),
                item.getName()
        );
    }

    public static ItemResponseDto makeItemForOwnerDto(ItemDto itemDto, BookingShortDto lastBooking, BookingShortDto nextBooking, List<CommentResponseDto> comments) {
        return new ItemResponseDto(
                itemDto,
                lastBooking,
                nextBooking,
                comments
        );
    }

    public static ItemForRequestDto makeItemForRequestDto(Item item) {
        return new ItemForRequestDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                item.getRequestItem().getId()
        );
    }
}
