package ru.practicum.shareit.booking.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

@Component
@RequiredArgsConstructor
public class BookingMapper {

    private final UserMapper userMapper;
    private final ItemMapper itemMapper;

    public Booking makeBooking(BookingRequestDto bookingRequestDto, Item item, User booker) {
        return new Booking(
                bookingRequestDto.getId(),
                bookingRequestDto.getStart(),
                bookingRequestDto.getEnd(),
                item,
                booker,
                BookingStatus.WAITING
        );
    }

    public BookingResponseDto makeBookingResponse(Booking booking) {
        return new BookingResponseDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                userMapper.makeUserShortDto(booking.getBooker()),
                itemMapper.makeItemShortDto(booking.getItem())
        );
    }

    public BookingShortDto makeBookingShortDto(Booking booking) {
        return new BookingShortDto(
                booking.getId(),
                booking.getBooker().getId()
        );
    }
}
