package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.dto.RequestItemRequestDto;
import ru.practicum.shareit.request.dto.RequestItemResponseDto;
import ru.practicum.shareit.request.dto.RequestItemShortResponseDto;
import ru.practicum.shareit.request.model.RequestItem;
import ru.practicum.shareit.request.service.RequestItemServiceImpl;
import ru.practicum.shareit.request.storage.RequestItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RequestServiceTest {

    @Mock
    private ItemStorage itemStorage;
    @Mock
    private UserStorage userStorage;
    @Mock
    private RequestItemStorage requestItemStorage;
    @InjectMocks
    RequestItemServiceImpl service;

    private User user;
    private User anotherUser;
    private Item item;
    private ItemForRequestDto itemForRequestDto;
    private RequestItem requestItem;
    private RequestItemRequestDto requestDto;
    private RequestItemResponseDto responseDto;
    private RequestItemShortResponseDto shortResponseDto;

    private final LocalDateTime created = LocalDateTime.of(2025, 8, 30,
            0, 0, 15);

    @BeforeEach
    void createBookingTestEnvironment() {
        user = new User(11L, "dtoName", "email@yandex.ru");
        anotherUser = new User(22L, "ownerName", "ownerEmail@yandex.ru");
        requestItem = new RequestItem(55L, "description", user, created);
        item = new Item(11L, "itemName", "itemDescription", true, user, requestItem);
        itemForRequestDto = new ItemForRequestDto(11L, "name", "description", true, 55L);
        requestDto = new RequestItemRequestDto("description");
        responseDto = new RequestItemResponseDto(55L, "description", created, List.of(itemForRequestDto));
        shortResponseDto = new RequestItemShortResponseDto(11L, "description", created);
    }

    @Test
    void addRequestAndThenOk() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(requestItemStorage.save(any())).thenReturn(requestItem);

        RequestItemShortResponseDto dto = service.addRequest(user.getId(), requestDto);

        assertThat(dto.getId(), equalTo(responseDto.getId()));
        assertThat(dto.getDescription(), equalTo(responseDto.getDescription()));
        assertThat(dto.getCreated(), equalTo(responseDto.getCreated()));
    }

    @Test
    void addRequestForWrongUser() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.addRequest(user.getId(), requestDto));
    }

    @Test
    void getOwnerRequestAndThenOk() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(requestItemStorage.findByRequestorIdOrderByCreatedDesc(user.getId())).thenReturn(List.of(requestItem));
        when(itemStorage.findByRequestItemInOrderById(List.of(requestItem))).thenReturn(List.of(item));

        List<RequestItemResponseDto> list = service.getMyRequests(user.getId());

        assertThat(list.size(), equalTo(1));
        assertThat(list.get(0).getDescription(), equalTo(responseDto.getDescription()));
        assertThat(list.get(0).getCreated(), equalTo(responseDto.getCreated()));
        assertThat(list.get(0).getItems().get(0).getName(), equalTo(item.getName()));
    }

    @Test
    void getOwnerRequestForWrongUser() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.getMyRequests(user.getId()));
    }

    @Test
    void getAllUserRequestAndThenOk() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(requestItemStorage.findByRequestorIdNotOrderByCreatedDesc(anyLong(), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(requestItem)));
        when(itemStorage.findByRequestItemInOrderById(List.of(requestItem))).thenReturn(List.of(item));

        List<RequestItemResponseDto> list = service.getAllUserRequests(anotherUser.getId(), 0, 1);

        assertThat(list.size(), equalTo(1));
        assertThat(list.get(0).getDescription(), equalTo(responseDto.getDescription()));
        assertThat(list.get(0).getCreated(), equalTo(responseDto.getCreated()));
        assertThat(list.get(0).getItems().get(0).getName(), equalTo(item.getName()));
    }

    @Test
    void getRequestByIdAndThenOk() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(requestItemStorage.findById(anyLong())).thenReturn(Optional.of(requestItem));
        when(itemStorage.findByRequestItemInOrderById(List.of(requestItem))).thenReturn(List.of(item));

        RequestItemResponseDto dto = service.getRequestItemResponseDto(user.getId(), requestItem.getId());

        assertThat(dto.getDescription(), equalTo(responseDto.getDescription()));
        assertThat(dto.getCreated(), equalTo(responseDto.getCreated()));
        assertThat(dto.getItems().get(0).getName(), equalTo(item.getName()));
    }
}
