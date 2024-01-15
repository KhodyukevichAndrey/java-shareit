package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.RequestItemRequestDto;
import ru.practicum.shareit.request.dto.RequestItemResponseDto;
import ru.practicum.shareit.request.dto.RequestItemShortResponseDto;
import ru.practicum.shareit.request.service.RequestItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest(properties = "db.name=test", webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class RequestItemIntegrationTest {

    private final UserService userService;
    private final ItemService itemService;
    private final RequestItemService requestItemService;

    private UserDto firstUserDto;
    private UserDto secondUserDto;
    private UserDto firstUserResponse;
    private UserDto secondUserResponse;
    private ItemDto firstItem;
    private ItemDto secondItem;
    private ItemDto firstItemResponse;
    private ItemDto secondItemResponse;
    private RequestItemRequestDto firstRequest;
    private RequestItemShortResponseDto firstRequestResponse;
    private RequestItemRequestDto secondRequest;
    private RequestItemShortResponseDto secondRequestResponse;
    private RequestItemRequestDto thirdRequest;
    private RequestItemShortResponseDto thirdRequestResponse;
    private RequestItemRequestDto fourthRequest;
    private RequestItemShortResponseDto fourthRequestResponse;

    @Test
    void getAllUsersRequestsAndThenOk() {
        createEnvironmentTest();

        List<RequestItemResponseDto> responseList = requestItemService.getAllUserRequests(secondUserResponse.getId(), 0, 4);

        assertThat(responseList.size(), equalTo(4));
        assertThat(responseList.get(3).getDescription(), equalTo(firstRequestResponse.getDescription()));
        assertThat(responseList.get(3).getCreated(), equalTo(firstRequestResponse.getCreated()));
        assertThat(responseList.get(3).getItems().get(0).getName(), equalTo(firstItemResponse.getName()));
        assertThat(responseList.get(3).getItems().get(0).getDescription(), equalTo(firstItemResponse.getDescription()));
    }

    private void createEnvironmentTest() {
        firstUserDto = new UserDto(0, "name", "email.yandex.ru");
        secondUserDto = new UserDto(0, "name", "anotherEmail.yandex.ru");
        firstUserResponse = userService.addUser(firstUserDto);
        secondUserResponse = userService.addUser(secondUserDto);

        firstRequest = new RequestItemRequestDto("Try to find shovel");
        secondRequest = new RequestItemRequestDto("Try to find table");
        thirdRequest = new RequestItemRequestDto("Try to find chairs for party");
        fourthRequest = new RequestItemRequestDto("Try to find spoons");
        firstRequestResponse = requestItemService.addRequest(firstUserResponse.getId(), firstRequest);
        secondRequestResponse = requestItemService.addRequest(firstUserResponse.getId(), secondRequest);
        thirdRequestResponse = requestItemService.addRequest(firstUserResponse.getId(), thirdRequest);
        fourthRequestResponse = requestItemService.addRequest(firstUserResponse.getId(), fourthRequest);

        firstItem = new ItemDto(0, "SHOVEL", "firstItemDescription",
                true, firstRequestResponse.getId());
        firstItemResponse = itemService.addItem(firstUserResponse.getId(), firstItem);
        secondItem = new ItemDto(0, "secondItemName",
                "secondItemDescription", true, null);
        secondItemResponse = itemService.addItem(secondUserResponse.getId(), secondItem);
    }
}
