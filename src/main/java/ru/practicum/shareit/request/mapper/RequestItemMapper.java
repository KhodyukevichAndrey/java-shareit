package ru.practicum.shareit.request.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.request.dto.RequestItemRequestDto;
import ru.practicum.shareit.request.dto.RequestItemResponseDto;
import ru.practicum.shareit.request.dto.RequestItemShortResponseDto;
import ru.practicum.shareit.request.model.RequestItem;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Component
public class RequestItemMapper {

    public static RequestItem makeRequestItem(RequestItemRequestDto requestDto, User user) {
        return new RequestItem(
                0,
                requestDto.getDescription(),
                user,
                LocalDateTime.now());
    }

    public static RequestItemResponseDto makeMyResponseDto(RequestItem requestItem, List<ItemForRequestDto> items) {
        return new RequestItemResponseDto(
                requestItem.getId(),
                requestItem.getDescription(),
                requestItem.getCreated(),
                items
        );
    }

    public static RequestItemResponseDto makeResponseWithoutAnswer(RequestItem requestItem) {
        return new RequestItemResponseDto(
                requestItem.getId(),
                requestItem.getDescription(),
                requestItem.getCreated(),
                Collections.emptyList()
        );
    }

    public static RequestItemShortResponseDto makeShortResponse(RequestItem requestItem) {
        return new RequestItemShortResponseDto(
                requestItem.getId(),
                requestItem.getDescription(),
                requestItem.getCreated()
        );
    }
}
