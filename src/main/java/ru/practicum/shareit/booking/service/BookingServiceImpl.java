package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
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
    private static final Sort SORT_BY_START_DESC = Sort.by(Sort.Direction.DESC, "start");

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
        List<Booking> bookingsByBookerId = findBookingsByBookerIdAndState(state, bookerId, LocalDateTime.now());

        return bookingsByBookerId.stream()
                .map(bookingMapper::makeBookingResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDto> getAllOwnerBooking(long ownerId, String state) {
        getUser(ownerId);
        List<Booking> bookingsByOwnerId = findBookingsByOwnerIdAndState(state, ownerId, LocalDateTime.now());

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

    private List<Booking> findBookingsByBookerIdAndState(String state, long bookerId, LocalDateTime now) {
        State s;
        try {
            s = State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedStateException("Unknown state: UNSUPPORTED_STATUS");
        }
        switch (s) {
            case ALL:
                return bookingStorage.findBookingByBookerId(bookerId, SORT_BY_START_DESC);
            case PAST:
                return bookingStorage.findAllBookingByBookerIdAndEndBefore(bookerId, now, SORT_BY_START_DESC);
            case CURRENT:
                return bookingStorage.findAllBookingByBookerIdAndStartBeforeAndEndAfter(bookerId, now, now,
                        SORT_BY_START_DESC);
            case FUTURE:
                return bookingStorage.findAllBookingByBookerIdAndStartAfter(bookerId, now, SORT_BY_START_DESC);
            case REJECTED:
                return bookingStorage.findAllBookingByBookerIdAndStatusIs(bookerId, BookingStatus.REJECTED,
                        SORT_BY_START_DESC);
            case WAITING:
                return bookingStorage.findAllBookingByBookerIdAndStatusIs(bookerId, BookingStatus.WAITING,
                        SORT_BY_START_DESC);
            default:
                throw new UnsupportedStateException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    private List<Booking> findBookingsByOwnerIdAndState(String state, long ownerId, LocalDateTime now) {
        State s;
        try {
            s = State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedStateException("Unknown state: UNSUPPORTED_STATUS");
        }
        switch (s) {
            case ALL:
                return bookingStorage.findBookingByItemOwnerId(ownerId, SORT_BY_START_DESC);
            case PAST:
                return bookingStorage.findAllBookingByItemOwnerIdAndEndBefore(ownerId, now, SORT_BY_START_DESC);
            case CURRENT:
                return bookingStorage.findAllBookingByItemOwnerIdAndStartBeforeAndEndAfter(ownerId, now, now,
                        SORT_BY_START_DESC);
            case FUTURE:
                return bookingStorage.findAllBookingByItemOwnerIdAndStartAfter(ownerId, now, SORT_BY_START_DESC);
            case REJECTED:
                return bookingStorage.findAllBookingByItemOwnerIdAndStatusIs(ownerId, BookingStatus.REJECTED,
                        SORT_BY_START_DESC);
            case WAITING:
                return bookingStorage.findAllBookingByItemOwnerIdAndStatusIs(ownerId, BookingStatus.WAITING,
                        SORT_BY_START_DESC);
            default:
                throw new UnsupportedStateException("Unknown state: UNSUPPORTED_STATUS");
        }
    }
}
