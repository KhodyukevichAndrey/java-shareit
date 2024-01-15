package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.exception.UnsupportedStateException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(properties = "db.name=test", webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingIntegrationTest {

    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;
    private UserDto firstUserDto;
    private UserDto secondUserDto;
    private UserDto firstUserResponse;
    private UserDto secondUserResponse;
    private ItemDto secondItem;
    private ItemDto thirdItem;
    private ItemDto secondItemResponse;
    private ItemDto thirdItemResponse;
    private BookingRequestDto firstBookingRequestDto;
    private BookingRequestDto secondBookingRequestDto;
    private BookingResponseDto firstBookingResponseDtoByUser1;
    private BookingResponseDto secondBookingResponseDtoByUser1;
    private final LocalDateTime firstStart = LocalDateTime.of(2050, 8, 29,
            0, 0, 15);
    private final LocalDateTime firstEnd = LocalDateTime.of(2050, 8, 30,
            0, 0, 15);
    private final LocalDateTime secondStart = LocalDateTime.of(2050, 8, 29,
            0, 0, 15);
    private final LocalDateTime secondEnd = LocalDateTime.of(2050, 8, 30,
            0, 0, 15);

    @Test
    void getAllOwnerBookingAndThenOk() {
        createEnvironmentTest();
        bookingService.confirmBooking(secondUserResponse.getId(), secondBookingResponseDtoByUser1.getId(), true);

        List<BookingResponseDto> secondUserBookings = bookingService.getAllOwnerBooking(secondUserResponse.getId(), "FUTURE", 0, 3);
        BookingResponseDto first = secondUserBookings.get(0);
        BookingResponseDto second = secondUserBookings.get(1);

        assertThat(secondUserBookings.size(), equalTo(2));
        assertThat(first.getStart(), equalTo(firstBookingResponseDtoByUser1.getStart()));
        assertThat(first.getEnd(), equalTo(firstBookingResponseDtoByUser1.getEnd()));
        assertThat(first.getBooker().getId(), equalTo(firstBookingResponseDtoByUser1.getBooker().getId()));
        assertThat(first.getItem().getName(), equalTo(firstBookingResponseDtoByUser1.getItem().getName()));
        assertThat(first.getStatus(), equalTo(BookingStatus.WAITING));
        assertThat(second.getStart(), equalTo(secondBookingResponseDtoByUser1.getStart()));
        assertThat(second.getEnd(), equalTo(secondBookingResponseDtoByUser1.getEnd()));
        assertThat(second.getBooker().getId(), equalTo(secondBookingResponseDtoByUser1.getBooker().getId()));
        assertThat(second.getItem().getName(), equalTo(secondBookingResponseDtoByUser1.getItem().getName()));
        assertThat(second.getStatus(), equalTo(BookingStatus.APPROVED));
    }

    @Test
    void getAllOwnerBookingWithWrongState() {
        createEnvironmentTest();

        assertThrows(UnsupportedStateException.class,
                () -> bookingService.getAllOwnerBooking(secondUserResponse.getId(), "UNSUPPORTED", 0, 3));
    }

    @Test
    void getAllOwnerBookingWith1ResultOnPageWhenBookingsIsMoreThanOne() {
        createEnvironmentTest();

        List<BookingResponseDto> secondUserBookings =
                bookingService.getAllOwnerBooking(secondUserResponse.getId(), "FUTURE", 0, 1);
        BookingResponseDto first = secondUserBookings.get(0);

        assertThat(secondUserBookings.size(), equalTo(1));
        assertThat(first.getStart(), equalTo(firstBookingResponseDtoByUser1.getStart()));
        assertThat(first.getEnd(), equalTo(firstBookingResponseDtoByUser1.getEnd()));
        assertThat(first.getBooker().getId(), equalTo(firstBookingResponseDtoByUser1.getBooker().getId()));
        assertThat(first.getItem().getName(), equalTo(firstBookingResponseDtoByUser1.getItem().getName()));
        assertThat(first.getStatus(), equalTo(BookingStatus.WAITING));

    }

    private void createEnvironmentTest() {
        firstUserDto = new UserDto(0, "name", "email.yandex.ru");
        secondUserDto = new UserDto(0, "name", "anotherEmail.yandex.ru");
        firstUserResponse = userService.addUser(firstUserDto);
        secondUserResponse = userService.addUser(secondUserDto);

        secondItem = new ItemDto(0, "secondItemName",
                "secondItemDescription", true, null);
        thirdItem = new ItemDto(0, "thirdItemName",
                "thirdItemDescription", true, null);
        secondItemResponse = itemService.addItem(secondUserResponse.getId(), secondItem);
        thirdItemResponse = itemService.addItem(secondUserResponse.getId(), thirdItem);

        firstBookingRequestDto = new BookingRequestDto(0, secondItemResponse.getId(), firstStart, firstEnd);
        secondBookingRequestDto = new BookingRequestDto(0, thirdItemResponse.getId(), secondStart, secondEnd);

        firstBookingResponseDtoByUser1 = bookingService.createBooking(firstBookingRequestDto,
                firstUserResponse.getId());
        secondBookingResponseDtoByUser1 = bookingService.createBooking(secondBookingRequestDto,
                firstUserResponse.getId());
    }
}
