package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.state.State;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.UnsupportedStateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingStorage bookingStorage;
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;
    private final BookingMapper bookingMapper;
    private static final String WRONG_USER_ID = "Пользователь с указанным ID не найден";
    private static final String WRONG_ITEM_ID = "Предмет с указанным ID не найден";
    private static final String WRONG_BOOKING_ID = "Предмет с указанным ID не найден";

    @Override
    @Transactional
    public BookingResponseDto createBooking(BookingRequestDto bookingRequestDto, long bookerId) {
        User booker = getUser(bookerId);
        Item item = getItem(bookingRequestDto.getItemId());

        if (item.getOwner().getId() == bookerId) {
            throw new EntityNotFoundException("Предмет не доступен для бронивания владельцу вещи");
        }

        if (item.isAvailable()) {
            Booking booking = bookingStorage.save(bookingMapper.makeBooking(bookingRequestDto, item, booker));
            return bookingMapper.makeBookingResponse(booking);
        } else {
            throw new NotAvailableException("Предмет не доступен для бронивания");
        }
    }

    @Override
    @Transactional
    public BookingResponseDto confirmBooking(long userId, long bookingId, boolean isConfirm) {
        Booking booking = getBooking(bookingId);
        checkBookingStatus(booking);

        long itemOwnerId = booking.getItem().getOwner().getId();

        if (itemOwnerId != userId) {
            throw new EntityNotFoundException("Изменить статус запроса может только владелец вещи");
        }

        if (isConfirm) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        bookingStorage.save(booking);

        return bookingMapper.makeBookingResponse(booking);
    }

    @Override
    public BookingResponseDto getBookingResponse(long userId, long bookingId) {
        Booking booking = getBooking(bookingId);
        User booker = booking.getBooker();
        Item item = booking.getItem();
        if (booker.getId() == userId || item.getOwner().getId() == userId) {
            return bookingMapper.makeBookingResponse(booking);
        } else {
            throw new EntityNotFoundException(
                    "Данные о booking могут быть запрошены либо владельцем вещи либо автором бронивания");
        }
    }

    @Override
    public List<BookingResponseDto> getAllUserBooking(long bookerId, String state) {
        getUser(bookerId);
        List<Booking> bookingsByBookerId = bookingStorage.findBookingByBookerId(bookerId);
        bookingsByBookerId = filterByState(state, bookingsByBookerId, LocalDateTime.now());

        return bookingsByBookerId.stream()
                .map(bookingMapper::makeBookingResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDto> getAllOwnerBooking(long ownerId, String state) {
        getUser(ownerId);
        List<Booking> bookingsByOwnerId = bookingStorage.getBookingByOwnerId(ownerId);
        bookingsByOwnerId = filterByState(state, bookingsByOwnerId, LocalDateTime.now());

        return bookingsByOwnerId.stream()
                .map((bookingMapper::makeBookingResponse))
                .collect(Collectors.toList());
    }

    private User getUser(long id) {
        return userStorage.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(WRONG_USER_ID));
    }

    private Item getItem(long id) {
        return itemStorage.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(WRONG_ITEM_ID));
    }

    private Booking getBooking(long id) {
        return bookingStorage.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(WRONG_BOOKING_ID));
    }

    private void checkBookingStatus(Booking booking) {
        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new NotAvailableException(
                    "Изменения статуса аренды допустимо для вещей только со статусом 'В ожидании'");
        }
    }

    private List<Booking> filterByState(String state, List<Booking> bookings, LocalDateTime now) {
        State s;
        try {
            s = State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedStateException("Unknown state: UNSUPPORTED_STATUS");
        }
        switch (s) {
            case ALL:
                return bookings;
            case PAST:
                return bookings.stream()
                        .filter(b -> b.getEnd().isBefore(now))
                        .collect(Collectors.toList());
            case CURRENT:
                return bookings.stream()
                        .filter((b -> b.getStart().isBefore(now) && b.getEnd().isAfter(now)))
                        .collect(Collectors.toList());
            case FUTURE:
                return bookings.stream()
                        .filter(b -> b.getStart().isAfter(now))
                        .collect(Collectors.toList());
            case REJECTED:
                return bookings.stream()
                        .filter(b -> b.getStatus().equals(BookingStatus.REJECTED))
                        .collect(Collectors.toList());
            case WAITING:
                return bookings.stream()
                        .filter(b -> b.getStatus().equals(BookingStatus.WAITING))
                        .collect(Collectors.toList());
            default:
                throw new UnsupportedStateException("Unknown state: UNSUPPORTED_STATUS");
        }
    }
}
