package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestItemRequestDto;
import ru.practicum.shareit.request.dto.RequestItemResponseDto;
import ru.practicum.shareit.request.dto.RequestItemShortResponseDto;
import ru.practicum.shareit.request.service.RequestItemService;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

import static ru.practicum.shareit.constants.headers.HeadersConstants.USER_ID;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {

    private final RequestItemService itemRequestService;

    @PostMapping
    public RequestItemShortResponseDto addRequest(@RequestHeader(USER_ID) long userId,
                                                  @RequestBody @Valid RequestItemRequestDto requestDto) {
        log.debug("Получен запрос POST /requests");
        return itemRequestService.addRequest(userId, requestDto);
    }

    @GetMapping
    public List<RequestItemResponseDto> getMyRequests(@RequestHeader(USER_ID) long userId) {
        log.debug("Получен запрос GET /requests");
        return itemRequestService.getMyRequests(userId);
    }

    @GetMapping("/all")
    public List<RequestItemResponseDto> getAllUsersRequests(@RequestHeader(USER_ID) long userId,
                                                            @RequestParam(defaultValue = "0") @Min(0) @Max(50) int from,
                                                            @RequestParam(defaultValue = "20") @Min(1) @Max(50) int size) {
        log.debug("Получен запрос GET /requests/all");
        return itemRequestService.getAllUserRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public RequestItemResponseDto getRequest(@RequestHeader(USER_ID) long userId,
                                             @PathVariable long requestId) {
        log.debug("Получен запрос GET /requests/{requestId}");
        return itemRequestService.getRequestItemResponseDto(userId, requestId);
    }
}
