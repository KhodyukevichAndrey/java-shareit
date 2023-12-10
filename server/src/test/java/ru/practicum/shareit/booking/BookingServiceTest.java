package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
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
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingStorage bookingStorage;
    @Mock
    private ItemStorage itemStorage;
    @Mock
    private UserStorage userStorage;
    @InjectMocks
    private BookingServiceImpl service;

    private final LocalDateTime start = LocalDateTime.of(2050, 8, 29,
            0, 0, 15);
    private final LocalDateTime end = LocalDateTime.of(2050, 8, 30,
            0, 0, 15);

    private User user;
    private User anotherUser;
    private Item item;
    private BookingRequestDto bookingRequestDto;
    private Booking booking;
    private Booking confirmedBooking;
    private Booking rejectedBooking;

    @BeforeEach
    void createBookingTestEnvironment() {
        user = new User(1L, "dtoName", "email@yandex.ru");
        anotherUser = new User(2L, "ownerName", "ownerEmail@yandex.ru");
        item = new Item(1L, "itemName", "itemDescription", true, user, null);
        bookingRequestDto = new BookingRequestDto(1L, 1L, start, end);
        booking = new Booking(1L, start, end, item, user, BookingStatus.WAITING);
        confirmedBooking = new Booking(1L, start, end, item, user, BookingStatus.APPROVED);
        rejectedBooking = new Booking(1L, start, end, item, user, BookingStatus.REJECTED);
    }

    @Test
    void createBooking() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemStorage.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingStorage.save(any())).thenReturn(booking);

        BookingResponseDto brd = service.createBooking(bookingRequestDto, 2L);

        assertThat(brd.getId(), equalTo(bookingRequestDto.getId()));
        assertThat(brd.getStart(), equalTo(bookingRequestDto.getStart()));
        assertThat(brd.getEnd(), equalTo(bookingRequestDto.getEnd()));
        assertThat(brd.getItem().getId(), equalTo(bookingRequestDto.getItemId()));
    }

    @Test
    void createBookingWithOwnerIdAndThenThrowsEntityNotFound() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemStorage.findById(anyLong())).thenReturn(Optional.of(item));

        assertThrows(EntityNotFoundException.class, () -> service.createBooking(bookingRequestDto, 1L));
    }

    @Test
    void createBookingWhenItemIsNotAvailableAndThenThrowsNotAvailable() {
        item.setAvailable(false);
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemStorage.findById(anyLong())).thenReturn(Optional.of(item));

        assertThrows(NotAvailableException.class, () -> service.createBooking(bookingRequestDto, 2L));
    }

    @Test
    void confirmBooking() {
        when(bookingStorage.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingStorage.save(any())).thenReturn(confirmedBooking);

        BookingResponseDto brd = service.confirmBooking(1L, 1L, true);

        assertThat(brd.getId(), equalTo(bookingRequestDto.getId()));
        assertThat(brd.getStart(), equalTo(bookingRequestDto.getStart()));
        assertThat(brd.getEnd(), equalTo(bookingRequestDto.getEnd()));
        assertThat(brd.getItem().getId(), equalTo(bookingRequestDto.getItemId()));
        assertThat(brd.getStatus(), equalTo(BookingStatus.APPROVED));
    }

    @Test
    void rejectBooking() {
        when(bookingStorage.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingStorage.save(any())).thenReturn(rejectedBooking);

        BookingResponseDto brd = service.confirmBooking(1L, 1L, false);

        assertThat(brd.getId(), equalTo(bookingRequestDto.getId()));
        assertThat(brd.getStart(), equalTo(bookingRequestDto.getStart()));
        assertThat(brd.getEnd(), equalTo(bookingRequestDto.getEnd()));
        assertThat(brd.getItem().getId(), equalTo(bookingRequestDto.getItemId()));
        assertThat(brd.getStatus(), equalTo(BookingStatus.REJECTED));
    }

    @Test
    void confirmBookingByAnotherUserAndThenThrowsEntityNotFound() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemStorage.findById(anyLong())).thenReturn(Optional.of(item));

        assertThrows(EntityNotFoundException.class, () -> service.createBooking(bookingRequestDto, 1L));
    }

    @Test
    void getBooking() {
        when(bookingStorage.findById(anyLong())).thenReturn(Optional.of(booking));

        BookingResponseDto brd = service.getBookingResponse(user.getId(), booking.getId());

        assertThat(brd.getId(), equalTo(booking.getId()));
        assertThat(brd.getStart(), equalTo(booking.getStart()));
        assertThat(brd.getEnd(), equalTo(booking.getEnd()));
        assertThat(brd.getItem().getId(), equalTo(booking.getItem().getId()));
        assertThat(brd.getStatus(), equalTo(BookingStatus.WAITING));
    }

    @Test
    void getBookingByAnotherUserAndThenThrowsEntityNotFound() {
        when(bookingStorage.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(EntityNotFoundException.class,
                () -> service.getBookingResponse(anotherUser.getId(), booking.getId()));
    }

    @Test
    void getAllUserBookings() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingStorage.findAllBookingByBookerIdAndStatusIs(anyLong(), any(), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingResponseDto> brd = service.getAllUserBooking(user.getId(), "WAITING", 0, 1);

        assertThat(brd.size(), equalTo(1));
        assertThat(brd.get(0).getStatus(), equalTo(booking.getStatus()));
        assertThat(brd.get(0).getStart(), equalTo(booking.getStart()));
        assertThat(brd.get(0).getEnd(), equalTo(booking.getEnd()));
    }

    @Test
    void getAllUserBookingsWithWrongState() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));

        assertThrows(UnsupportedStateException.class,
                () -> service.getAllUserBooking(user.getId(), "WrongState", 0, 1));
    }

    @Test
    void getAllOwnerBookings() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingStorage.findAllBookingByItemOwnerIdAndStatusIs(anyLong(), any(), any(Pageable.class)))
                .thenReturn(List.of(rejectedBooking));

        List<BookingResponseDto> brd = service.getAllOwnerBooking(user.getId(), "REJECTED", 0, 1);

        assertThat(brd.size(), equalTo(1));
        assertThat(brd.get(0).getStatus(), equalTo(rejectedBooking.getStatus()));
        assertThat(brd.get(0).getStart(), equalTo(rejectedBooking.getStart()));
        assertThat(brd.get(0).getEnd(), equalTo(rejectedBooking.getEnd()));
    }

    @Test
    void getAllOwnerBookingsWithWrongState() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));

        assertThrows(UnsupportedStateException.class,
                () -> service.getAllOwnerBooking(user.getId(), "WrongState", 0, 1));
    }
}
