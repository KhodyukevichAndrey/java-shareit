package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.item.storage.CommentStorage;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.model.RequestItem;
import ru.practicum.shareit.request.storage.RequestItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private BookingStorage bookingStorage;
    @Mock
    private ItemStorage itemStorage;
    @Mock
    private UserStorage userStorage;
    @Mock
    private RequestItemStorage requestItemStorage;
    @Mock
    private CommentStorage commentStorage;
    @InjectMocks
    private ItemServiceImpl service;

    private final LocalDateTime start = LocalDateTime.of(2050, 8, 29,
            0, 0, 15);
    private final LocalDateTime end = LocalDateTime.of(2050, 8, 30,
            0, 0, 15);
    private final LocalDateTime created = LocalDateTime.of(2025, 8, 30,
            0, 0, 15);

    private User user;
    private User anotherUser;
    private Booking lastBooking;
    private Booking nextBooking;
    private ItemDto itemDtoNoRequest;
    private ItemDto itemDtoWithRequest;
    private Item itemNoRequest;
    private Item itemWithRequest;
    private RequestItem requestItem;
    private Comment comment;
    private CommentRequestDto commentRequestDto;
    private CommentResponseDto commentResponseDto;
    private BookingShortDto lastShortBooking;
    private BookingShortDto nextShortBooking;
    private ItemResponseDto itemResponseDtoWithBooking;

    @BeforeEach
    void createItemTestEnvironment() {
        user = new User(11L, "dtoName", "email@yandex.ru");
        anotherUser = new User(22L, "ownerName", "ownerEmail@yandex.ru");
        requestItem = new RequestItem(11L, "requestDescription", user, created);
        itemDtoNoRequest = new ItemDto(11L, "firstDtoName", "firstDtoDescription", true, null);
        itemDtoWithRequest = new ItemDto(22L, "secondDtoName", "secondDtoDescription", true, 1L);
        itemNoRequest = new Item(11L, "firstDtoName", "firstDtoDescription", true, user, null);
        itemWithRequest = new Item(22L, "secondDtoName", "secondDtoDescription", true, anotherUser, requestItem);
        comment = new Comment(11L, "text", itemNoRequest, user, created);
        lastBooking = new Booking(11L, start, end, itemWithRequest, user, BookingStatus.WAITING);
        nextBooking = new Booking(22L, start.plusDays(5), end.plusDays(5), itemWithRequest, user, BookingStatus.WAITING);
        commentRequestDto = new CommentRequestDto("text");
        commentResponseDto = new CommentResponseDto(11L, "text", user.getName(), created);
        lastShortBooking = new BookingShortDto(11L, 11L);
        nextShortBooking = new BookingShortDto(22L, 11L);
        itemResponseDtoWithBooking = new ItemResponseDto(itemDtoWithRequest, lastShortBooking, nextShortBooking, List.of(commentResponseDto));
    }

    @Test
    void createItemAndThenOk() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemStorage.save(any())).thenReturn(itemNoRequest);

        ItemDto itemDto = service.addItem(user.getId(), itemDtoNoRequest);

        assertThat(itemDto.getId(), equalTo(itemNoRequest.getId()));
        assertThat(itemDto.getName(), equalTo(itemNoRequest.getName()));
        assertThat(itemDto.getDescription(), equalTo(itemNoRequest.getDescription()));
        assertThat(itemDto.getAvailable(), equalTo(itemNoRequest.isAvailable()));
    }

    @Test
    void createItemWithNoUserExistThenThrowsEntityNotFound() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.addItem(user.getId(), itemDtoNoRequest));
    }

    @Test
    void createItemForRequestAndThenOk() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemStorage.save(any())).thenReturn(itemWithRequest);
        when(requestItemStorage.findById(anyLong())).thenReturn(Optional.of(requestItem));

        ItemDto itemDto = service.addItem(user.getId(), itemDtoWithRequest);

        assertThat(itemDto.getId(), equalTo(itemWithRequest.getId()));
        assertThat(itemDto.getName(), equalTo(itemWithRequest.getName()));
        assertThat(itemDto.getDescription(), equalTo(itemWithRequest.getDescription()));
        assertThat(itemDto.getAvailable(), equalTo(itemWithRequest.isAvailable()));
        assertThat(itemDto.getRequestId(), equalTo(itemWithRequest.getRequestItem().getId()));
    }

    @Test
    void createItemWithWrongRequestId() {
        itemDtoWithRequest.setRequestId(14L);
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(requestItemStorage.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.addItem(user.getId(), itemDtoWithRequest));
    }

    @Test
    void updateItemAndThenOk() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemStorage.findById(anyLong())).thenReturn(Optional.of(itemNoRequest));
        when(itemStorage.save(any())).thenReturn(itemNoRequest);

        ItemDto itemDto = service.updateItem(user.getId(), itemDtoNoRequest.getId(), itemDtoNoRequest);

        assertThat(itemDto.getId(), equalTo(itemNoRequest.getId()));
        assertThat(itemDto.getName(), equalTo(itemNoRequest.getName()));
        assertThat(itemDto.getDescription(), equalTo(itemNoRequest.getDescription()));
        assertThat(itemDto.getAvailable(), equalTo(itemNoRequest.isAvailable()));
    }

    @Test
    void updateItemWithWrongUserId() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(anotherUser));
        when(itemStorage.findById(anyLong())).thenReturn(Optional.of(itemNoRequest));

        assertThrows(EntityNotFoundException.class,
                () -> service.updateItem(22L, itemDtoNoRequest.getId(), itemDtoNoRequest));
    }

    @Test
    void findItemByIdAndThenOk() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(anotherUser));
        when(itemStorage.findById(anyLong())).thenReturn(Optional.of(itemWithRequest));
        when(commentStorage.findCommentsByItemId(anyLong(), any())).thenReturn(List.of(comment));
        when(bookingStorage.findBookingsByItemIdAndStatusNot(anyLong(), any(), any())).thenReturn(List.of(lastBooking, nextBooking));

        ItemResponseDto itemDto = service.getItemDto(anotherUser.getId(), itemDtoWithRequest.getId());

        assertThat(itemDto.getId(), equalTo(itemWithRequest.getId()));
        assertThat(itemDto.getName(), equalTo(itemWithRequest.getName()));
        assertThat(itemDto.getDescription(), equalTo(itemWithRequest.getDescription()));
        assertThat(itemDto.getAvailable(), equalTo(itemWithRequest.isAvailable()));
        assertThat(itemDto.getComments().get(0), equalTo(commentResponseDto));
        assertThat(itemDto.getNextBooking().getId(), equalTo(itemResponseDtoWithBooking.getNextBooking().getId()));
    }

    @Test
    void findItemByIdForNotOwner() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemStorage.findById(anyLong())).thenReturn(Optional.of(itemWithRequest));
        when(commentStorage.findCommentsByItemId(anyLong(), any())).thenReturn(List.of(comment));

        ItemResponseDto itemDto = service.getItemDto(anotherUser.getId(), itemDtoWithRequest.getId());

        assertThat(itemDto.getId(), equalTo(itemWithRequest.getId()));
        assertThat(itemDto.getName(), equalTo(itemWithRequest.getName()));
        assertThat(itemDto.getDescription(), equalTo(itemWithRequest.getDescription()));
        assertThat(itemDto.getAvailable(), equalTo(itemWithRequest.isAvailable()));
        assertThat(itemDto.getComments().get(0), equalTo(commentResponseDto));
        assertNull(itemDto.getNextBooking());
        assertNull(itemDto.getLastBooking());
    }

    @Test
    void findItemByWrongId() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(anotherUser));
        when(itemStorage.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> service.getItemDto(anotherUser.getId(), 33L));
    }

    @Test
    void findAllOwnerItemsAndThenOk() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemStorage.findByOwnerIdOrderById(anyLong(), any(PageRequest.class))).thenReturn(List.of(itemNoRequest));

        List<ItemResponseDto> itemDtoList = service.getAllOwnersItems(user.getId(), 0, 1);

        assertThat(itemDtoList.size(), equalTo(1));
        assertThat(itemDtoList.get(0).getId(), equalTo(itemDtoNoRequest.getId()));
        assertThat(itemDtoList.get(0).getName(), equalTo(itemDtoNoRequest.getName()));
        assertThat(itemDtoList.get(0).getDescription(), equalTo(itemDtoNoRequest.getDescription()));
        assertThat(itemDtoList.get(0).getAvailable(), equalTo(itemDtoNoRequest.getAvailable()));
    }

    @Test
    void findAllOwnerItemsForWrongUserId() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.getAllOwnersItems(user.getId(), 0, 1));
    }

    @Test
    void deleteItemWithWrongOwnerId() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemStorage.findById(anyLong())).thenReturn(Optional.of(itemWithRequest));

        assertThrows(EntityNotFoundException.class,
                () -> service.deleteItem(user.getId(), itemWithRequest.getId()));
    }

    @Test
    void searchItemsAndThenOk() {
        when(itemStorage.searchItem(anyString(), any(PageRequest.class))).thenReturn(List.of(itemNoRequest));

        List<ItemDto> itemDtoList = service.searchItems("text", 0, 1);

        assertThat(itemDtoList.size(), equalTo(1));
        assertThat(itemDtoList.get(0).getId(), equalTo(itemDtoNoRequest.getId()));
        assertThat(itemDtoList.get(0).getName(), equalTo(itemDtoNoRequest.getName()));
        assertThat(itemDtoList.get(0).getDescription(), equalTo(itemDtoNoRequest.getDescription()));
        assertThat(itemDtoList.get(0).getAvailable(), equalTo(itemDtoNoRequest.getAvailable()));
    }

    @Test
    void searchItemsWithNoText() {
        List<ItemDto> itemDtoList = service.searchItems("", 0, 1);

        assertThat(itemDtoList.size(), equalTo(0));
    }

    @Test
    void addCommentAndThenOk() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemStorage.findById(anyLong())).thenReturn(Optional.of(itemWithRequest));
        when(bookingStorage.existsByItemIdAndEndBeforeAndBookerIdIs(anyLong(), any(LocalDateTime.class), anyLong()))
                .thenReturn(true);
        when(commentStorage.save(any())).thenReturn(comment);

        CommentResponseDto crd = service.addComment(commentRequestDto, user.getId(), itemWithRequest.getId());

        assertThat(crd.getId(), equalTo(comment.getId()));
        assertThat(crd.getText(), equalTo(comment.getText()));
        assertThat(crd.getAuthorName(), equalTo(comment.getAuthor().getName()));
        assertThat(crd.getCreated(), equalTo(comment.getCreated()));
    }

    @Test
    void addCommentWithoutBooking() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemStorage.findById(anyLong())).thenReturn(Optional.of(itemWithRequest));
        when(bookingStorage.existsByItemIdAndEndBeforeAndBookerIdIs(anyLong(), any(LocalDateTime.class), anyLong()))
                .thenReturn(false);

        assertThrows(NotAvailableException.class,
                () -> service.addComment(commentRequestDto, user.getId(), itemWithRequest.getId()));
    }
}
