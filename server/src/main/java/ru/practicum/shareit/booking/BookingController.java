package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

import static ru.practicum.shareit.constants.headers.HeadersConstants.USER_ID;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDto createBooking(@RequestHeader(USER_ID) long userId,
                                            @RequestBody BookingRequestDto bookingRequestDto) {
        return bookingService.createBooking(bookingRequestDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto confirmBooking(@RequestHeader(USER_ID) long userId,
                                             @PathVariable long bookingId,
                                             @RequestParam boolean approved) {
        return bookingService.confirmBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBookingResponse(@RequestHeader(USER_ID) long userId,
                                                 @PathVariable long bookingId) {
        return bookingService.getBookingResponse(userId, bookingId);
    }

    @GetMapping
    public List<BookingResponseDto> getAllUserBookings(@RequestHeader(USER_ID) long userId,
                                                       @RequestParam(defaultValue = "ALL")
                                                       String state,
                                                       @RequestParam(defaultValue = "0") int from,
                                                       @RequestParam(defaultValue = "20") int size) {
        return bookingService.getAllUserBooking(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getAllOwnerBookings(@RequestHeader(USER_ID) long userId,
                                                        @RequestParam(defaultValue = "ALL")
                                                        String state,
                                                        @RequestParam(defaultValue = "0") int from,
                                                        @RequestParam(defaultValue = "20") int size) {
        return bookingService.getAllOwnerBooking(userId, state, from, size);
    }
}
