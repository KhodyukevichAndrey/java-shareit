package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestItemRequestDto;
import ru.practicum.shareit.request.dto.RequestItemResponseDto;
import ru.practicum.shareit.request.dto.RequestItemShortResponseDto;
import ru.practicum.shareit.request.service.RequestItemService;

import java.util.List;

import static ru.practicum.shareit.constants.headers.HeadersConstants.USER_ID;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final RequestItemService itemRequestService;

    @PostMapping
    public RequestItemShortResponseDto addRequest(@RequestHeader(USER_ID) long userId,
                                                  @RequestBody RequestItemRequestDto requestDto) {
        return itemRequestService.addRequest(userId, requestDto);
    }

    @GetMapping
    public List<RequestItemResponseDto> getMyRequests(@RequestHeader(USER_ID) long userId) {
        return itemRequestService.getMyRequests(userId);
    }

    @GetMapping("/all")
    public List<RequestItemResponseDto> getAllUsersRequests(@RequestHeader(USER_ID) long userId,
                                                            @RequestParam(defaultValue = "0") int from,
                                                            @RequestParam(defaultValue = "20") int size) {
        return itemRequestService.getAllUserRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public RequestItemResponseDto getRequest(@RequestHeader(USER_ID) long userId,
                                             @PathVariable long requestId) {
        return itemRequestService.getRequestItemResponseDto(userId, requestId);
    }
}
