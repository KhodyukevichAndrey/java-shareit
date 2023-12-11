package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import static ru.practicum.shareit.constants.headers.Headers.USER_ID;

@Controller
@RequestMapping("/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {

    private final ItemRequestClient client;

    @PostMapping
    public ResponseEntity<Object> addRequest(@RequestHeader(USER_ID) long userId,
                                             @RequestBody @Valid RequestItemRequestDto requestDto) {
        log.debug("Получен запрос POST /requests");
        return client.addRequest(userId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getMyRequests(@RequestHeader(USER_ID) long userId) {
        log.debug("Получен запрос GET /requests");
        return client.getMyRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllUsersRequests(@RequestHeader(USER_ID) long userId,
                                                      @RequestParam(defaultValue = "0") @Min(0) @Max(50) int from,
                                                      @RequestParam(defaultValue = "20") @Min(1) @Max(50) int size) {
        log.debug("Получен запрос GET /requests/all");
        return client.getAllUsersRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@RequestHeader(USER_ID) long userId,
                                             @PathVariable long requestId) {
        log.debug("Получен запрос GET /requests/{requestId}");
        return client.getRequest(userId, requestId);
    }
}
