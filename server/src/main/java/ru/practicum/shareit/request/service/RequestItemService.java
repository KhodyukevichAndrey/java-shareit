package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.RequestItemRequestDto;
import ru.practicum.shareit.request.dto.RequestItemResponseDto;
import ru.practicum.shareit.request.dto.RequestItemShortResponseDto;

import java.util.List;

public interface RequestItemService {
    RequestItemShortResponseDto addRequest(long userId, RequestItemRequestDto requestDto);

    List<RequestItemResponseDto> getMyRequests(long userId);

    List<RequestItemResponseDto> getAllUserRequests(long userId, int from, int size);

    RequestItemResponseDto getRequestItemResponseDto(long userId, long requestId);
}
