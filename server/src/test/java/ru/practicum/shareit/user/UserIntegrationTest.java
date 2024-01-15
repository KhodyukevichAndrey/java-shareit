package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest(properties = "db.name=test", webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserIntegrationTest {

    private final UserService userService;

    private UserDto firstUserDto;
    private UserDto firstUserResponse;

    @Test
    void updateUserAndThenOk() {
        createEnvironmentTest();
        firstUserDto.setEmail("newEmail@yandex.ru");

        UserDto dto = userService.updateUser(firstUserResponse.getId(), firstUserDto);

        assertThat(dto.getName(), equalTo(firstUserResponse.getName()));
        assertThat(dto.getEmail(), equalTo("newEmail@yandex.ru"));
    }

    @Test
    void updateUserWithEmptyNameAndEmailAndThenNothingChanges() {
        createEnvironmentTest();
        firstUserDto.setEmail("");
        firstUserDto.setName("");

        UserDto dto = userService.updateUser(firstUserResponse.getId(), firstUserDto);

        assertThat(dto.getName(), equalTo(firstUserResponse.getName()));
        assertThat(dto.getEmail(), equalTo(firstUserResponse.getEmail()));
    }

    private void createEnvironmentTest() {
        firstUserDto = new UserDto(0, "name", "email.yandex.ru");
        firstUserResponse = userService.addUser(firstUserDto);
    }
}
