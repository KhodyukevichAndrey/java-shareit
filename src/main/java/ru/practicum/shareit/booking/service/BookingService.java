package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {

    BookingResponseDto createBooking(BookingRequestDto bookingRequestDto, long bookerId);

    BookingResponseDto confirmBooking(long userId, long bookingId, boolean isConfirm);

    BookingResponseDto getBookingResponse(long userId, long id);

    List<BookingResponseDto> getAllUserBooking(long bookerId, String state, int from, int size);

    List<BookingResponseDto> getAllOwnerBooking(long ownerId, String state, int from, int size);
}
