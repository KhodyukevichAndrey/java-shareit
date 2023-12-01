package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
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

import static ru.practicum.shareit.constants.error.ErrorConstants.*;
import static ru.practicum.shareit.constants.sort.SortConstants.SORT_BY_START_DESC;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingStorage bookingStorage;
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;

    @Override
    @Transactional
    public BookingResponseDto createBooking(BookingRequestDto bookingRequestDto, long bookerId) {
        User booker = getUser(bookerId);
        Item item = getItem(bookingRequestDto.getItemId());

        if (item.getOwner().getId() == bookerId) {
            throw new EntityNotFoundException("Предмет не доступен для бронивания владельцу вещи");
        }

        if (item.isAvailable()) {
            Booking booking = bookingStorage.save(BookingMapper.makeBooking(bookingRequestDto, item, booker));
            return BookingMapper.makeBookingResponse(booking);
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

        return BookingMapper.makeBookingResponse(booking);
    }

    @Override
    public BookingResponseDto getBookingResponse(long userId, long bookingId) {
        Booking booking = getBooking(bookingId);
        User booker = booking.getBooker();
        Item item = booking.getItem();
        if (booker.getId() == userId || item.getOwner().getId() == userId) {
            return BookingMapper.makeBookingResponse(booking);
        } else {
            throw new EntityNotFoundException(
                    "Данные о booking могут быть запрошены либо владельцем вещи либо автором бронивания");
        }
    }

    @Override
    public List<BookingResponseDto> getAllUserBooking(long bookerId, String state, int from, int size) {
        getUser(bookerId);
        List<Booking> bookingsByBookerId = findBookingsByBookerIdAndState(state,
                bookerId,
                LocalDateTime.now(),
                PageRequest.of(from / size, size, SORT_BY_START_DESC));

        return bookingsByBookerId.stream()
                .map(BookingMapper::makeBookingResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDto> getAllOwnerBooking(long ownerId, String state, int from, int size) {
        getUser(ownerId);
        List<Booking> bookingsByOwnerId = findBookingsByOwnerIdAndState(state,
                ownerId,
                LocalDateTime.now(),
                PageRequest.of(from / size, size, SORT_BY_START_DESC));

        return bookingsByOwnerId.stream()
                .map((BookingMapper::makeBookingResponse))
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

    private List<Booking> findBookingsByBookerIdAndState(String state, long bookerId, LocalDateTime now, PageRequest pr) {
        State s;
        try {
            s = State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedStateException("Unknown state: UNSUPPORTED_STATUS");
        }
        switch (s) {
            case ALL:
                return bookingStorage.findBookingByBookerId(bookerId, pr);
            case PAST:
                return bookingStorage.findAllBookingByBookerIdAndEndBefore(bookerId, now, pr);
            case CURRENT:
                return bookingStorage.findAllBookingByBookerIdAndStartBeforeAndEndAfter(bookerId, now, now, pr);
            case FUTURE:
                return bookingStorage.findAllBookingByBookerIdAndStartAfter(bookerId, now, pr);
            case REJECTED:
                return bookingStorage.findAllBookingByBookerIdAndStatusIs(bookerId, BookingStatus.REJECTED, pr);
            case WAITING:
                return bookingStorage.findAllBookingByBookerIdAndStatusIs(bookerId, BookingStatus.WAITING, pr);
            default:
                throw new UnsupportedStateException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    private List<Booking> findBookingsByOwnerIdAndState(String state, long ownerId, LocalDateTime now, PageRequest pr) {
        State s;
        try {
            s = State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedStateException("Unknown state: UNSUPPORTED_STATUS");
        }
        switch (s) {
            case ALL:
                return bookingStorage.findBookingByItemOwnerId(ownerId, pr);
            case PAST:
                return bookingStorage.findAllBookingByItemOwnerIdAndEndBefore(ownerId, now, pr);
            case CURRENT:
                return bookingStorage.findAllBookingByItemOwnerIdAndStartBeforeAndEndAfter(ownerId, now, now, pr);
            case FUTURE:
                return bookingStorage.findAllBookingByItemOwnerIdAndStartAfter(ownerId, now, pr);
            case REJECTED:
                return bookingStorage.findAllBookingByItemOwnerIdAndStatusIs(ownerId, BookingStatus.REJECTED, pr);
            case WAITING:
                return bookingStorage.findAllBookingByItemOwnerIdAndStatusIs(ownerId, BookingStatus.WAITING, pr);
            default:
                throw new UnsupportedStateException("Unknown state: UNSUPPORTED_STATUS");
        }
    }
}
