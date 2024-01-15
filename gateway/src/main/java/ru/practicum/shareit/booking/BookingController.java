package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import static ru.practicum.shareit.constants.headers.Headers.USER_ID;

@Controller
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader(USER_ID) long userId,
                                                @RequestBody @Valid BookingRequestDto requestDto) {
        log.debug("Получен запрос POST /bookings");
        return bookingClient.createBooking(userId, requestDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> confirmBooking(@RequestHeader(USER_ID) long userId,
                                                 @PathVariable long bookingId,
                                                 @RequestParam boolean approved) {
        log.debug("Получен запрос PATCH /bookings/{bookingId}?approved={approved}");
        return bookingClient.confirmBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingResponse(@RequestHeader(USER_ID) long userId,
                                                     @PathVariable Long bookingId) {
        log.debug("Получен запрос GET /bookings/{bookingId}");
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUserBookings(@RequestHeader(USER_ID) long userId,
                                                     @RequestParam(defaultValue = "ALL")
                                                     String state,
                                                     @RequestParam(defaultValue = "0") @Min(0) @Max(50)
                                                     int from,
                                                     @RequestParam(defaultValue = "20") @Min(1) @Max(50)
                                                     int size) {
        log.debug("Получен запрос GET /bookings?state={state}");
        return bookingClient.getAllUserBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllOwnerBookings(@RequestHeader(USER_ID) long userId,
                                                      @RequestParam(defaultValue = "ALL")
                                                      String state,
                                                      @RequestParam(defaultValue = "0") @Min(0) @Max(50)
                                                      int from,
                                                      @RequestParam(defaultValue = "20") @Min(1) @Max(50)
                                                      int size) {
        log.debug("Получен запрос GET /bookings/owner?state={state}");
        return bookingClient.getAllOwnerBookings(userId, state, from, size);
    }
}