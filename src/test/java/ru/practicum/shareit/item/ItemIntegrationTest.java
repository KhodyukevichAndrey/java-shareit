package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest(properties = "db.name=test", webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemIntegrationTest {

    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;

    private UserDto firstUserDto;
    private UserDto secondUserDto;
    private UserDto firstUserResponse;
    private UserDto secondUserResponse;
    private ItemDto firstItem;
    private ItemDto secondItem;
    private ItemDto firstItemResponse;
    private ItemDto secondItemResponse;
    private BookingRequestDto firstBookingRequestDto;
    private BookingRequestDto secondBookingRequestDto;
    private BookingResponseDto firstBookingResponseDtoByUser1;
    private BookingResponseDto secondBookingResponseDtoByUser1;
    private CommentRequestDto commentRequestDto;
    private CommentResponseDto commentResponseDto;

    private final LocalDateTime secondStart = LocalDateTime.of(2050, 8, 29,
            0, 0, 15);
    private final LocalDateTime secondEnd = LocalDateTime.of(2050, 8, 30,
            0, 0, 15);

    @Test
    void getItemResponseDtoByOwnerAndThenOk() throws InterruptedException {
        createEnvironmentTest();

        ItemResponseDto responseDto = itemService.getItemDto(secondUserResponse.getId(), secondItemResponse.getId());

        assertThat(responseDto.getId(), equalTo(secondItemResponse.getId()));
        assertThat(responseDto.getNextBooking().getBookerId(), equalTo(firstUserResponse.getId()));
        assertThat(responseDto.getDescription(), equalTo(secondItemResponse.getDescription()));
        assertThat(responseDto.getComments().size(), equalTo(1));
        assertThat(responseDto.getComments().get(0), equalTo(commentResponseDto));
    }

    @Test
    void getItemResponseDtoByAnotherUserAndThenOk() throws InterruptedException {
        createEnvironmentTest();

        ItemResponseDto responseDto = itemService.getItemDto(firstUserResponse.getId(), secondItemResponse.getId());

        assertThat(responseDto.getId(), equalTo(secondItemResponse.getId()));
        assertThat(responseDto.getNextBooking(), equalTo(null));
        assertThat(responseDto.getDescription(), equalTo(secondItemResponse.getDescription()));
        assertThat(responseDto.getComments().size(), equalTo(1));
        assertThat(responseDto.getComments().get(0), equalTo(commentResponseDto));
    }


    private void createEnvironmentTest() throws InterruptedException {
        firstUserDto = new UserDto(0, "name", "email.yandex.ru");
        secondUserDto = new UserDto(0, "name", "anotherEmail.yandex.ru");
        firstUserResponse = userService.addUser(firstUserDto);
        secondUserResponse = userService.addUser(secondUserDto);

        firstItem = new ItemDto(0, "firstItem", "firstItemDescription", true, null);
        secondItem = new ItemDto(0, "secondItemName",
                "secondItemDescription", true, null);
        firstItemResponse = itemService.addItem(firstUserResponse.getId(), firstItem);
        secondItemResponse = itemService.addItem(secondUserResponse.getId(), secondItem);

        firstBookingRequestDto = new BookingRequestDto(0, secondItemResponse.getId(), LocalDateTime.now(), LocalDateTime.now().plusSeconds(1));
        secondBookingRequestDto = new BookingRequestDto(0, secondItemResponse.getId(), secondStart, secondEnd);
        firstBookingResponseDtoByUser1 = bookingService.createBooking(firstBookingRequestDto,
                firstUserResponse.getId());
        secondBookingResponseDtoByUser1 = bookingService.createBooking(secondBookingRequestDto,
                firstUserResponse.getId());
        bookingService.confirmBooking(secondUserResponse.getId(), firstBookingResponseDtoByUser1.getId(), true);

        Thread.sleep(2000); // Для возможности оставить комментарий
        commentRequestDto = new CommentRequestDto("firstComment");
        commentResponseDto = itemService.addComment(commentRequestDto,
                firstUserResponse.getId(), secondItemResponse.getId());
    }
}
