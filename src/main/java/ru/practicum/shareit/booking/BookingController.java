package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

import static ru.practicum.shareit.constants.headers.HeadersConstants.USER_ID;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDto createBooking(@RequestHeader(USER_ID) long userId,
                                            @RequestBody @Valid BookingRequestDto bookingRequestDto) {
        log.debug("Получен запрос POST /bookings");
        return bookingService.createBooking(bookingRequestDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto confirmBooking(@RequestHeader(USER_ID) long userId,
                                             @PathVariable long bookingId,
                                             @RequestParam boolean approved) {
        log.debug("Получен запрос PATCH /bookings/{bookingId}?approved={approved}");
        return bookingService.confirmBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBookingResponse(@RequestHeader(USER_ID) long userId,
                                                 @PathVariable long bookingId) {
        log.debug("Получен запрос GET /bookings/{bookingId}");
        return bookingService.getBookingResponse(userId, bookingId);
    }

    @GetMapping
    public List<BookingResponseDto> getAllUserBookings(@RequestHeader(USER_ID) long userId,
                                                       @RequestParam(defaultValue = "ALL")
                                                       String state,
                                                       @Valid @RequestParam(defaultValue = "0") @Min(0) @Max(50)
                                                           int from,
                                                       @Valid @RequestParam(defaultValue = "20") @Min(1) @Max(50)
                                                           int size) {
        log.debug("Получен запрос GET /bookings?state={state}");
        return bookingService.getAllUserBooking(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getAllOwnerBookings(@RequestHeader(USER_ID) long userId,
                                                        @RequestParam(defaultValue = "ALL")
                                                        String state,
                                                        @Valid @RequestParam(defaultValue = "0") @Min(0) @Max(50)
                                                            int from,
                                                        @Valid @RequestParam(defaultValue = "20") @Min(1) @Max(50)
                                                            int size) {
        log.debug("Получен запрос GET /bookings/owner?state={state}");
        return bookingService.getAllOwnerBooking(userId, state, from, size);
    }
}
