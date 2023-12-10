package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserStorage userStorage;
    @InjectMocks
    private UserServiceImpl service;

    private User user;
    private User anotherUser;
    private UserDto userDto;

    @BeforeEach
    void createBookingTestEnvironment() {
        user = new User(1L, "dtoName", "email@yandex.ru");
        anotherUser = new User(2L, "ownerName", "ownerEmail@yandex.ru");
        userDto = new UserDto(1L, "dtoName", "email@yandex.ru");
    }

    @Test
    void addUserAndThenOk() {
        when(userStorage.save(any())).thenReturn(user);

        UserDto dto = service.addUser(userDto);

        assertThat(dto.getId(), equalTo(user.getId()));
        assertThat(dto.getName(), equalTo(user.getName()));
        assertThat(dto.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    void updateUserAndThenOk() {
        when(userStorage.findById(any())).thenReturn(Optional.of(user));
        when(userStorage.save(any())).thenReturn(user);

        UserDto dto = service.updateUser(user.getId(), userDto);

        assertThat(dto.getId(), equalTo(user.getId()));
        assertThat(dto.getName(), equalTo(user.getName()));
        assertThat(dto.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    void updateUserWithoutNameAndEmailAndThenOk() {
        userDto.setName("");
        userDto.setEmail("");
        when(userStorage.findById(any())).thenReturn(Optional.of(user));
        when(userStorage.save(any())).thenReturn(user);

        UserDto dto = service.updateUser(user.getId(), userDto);

        assertThat(dto.getId(), equalTo(user.getId()));
        assertThat(dto.getName(), equalTo(user.getName()));
        assertThat(dto.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    void findUserAndThenOk() {
        when(userStorage.findById(any())).thenReturn(Optional.of(user));

        UserDto dto = service.getUserDto(user.getId());

        assertThat(dto.getId(), equalTo(user.getId()));
        assertThat(dto.getName(), equalTo(user.getName()));
        assertThat(dto.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    void findUserByWrongIdAndThenThrowsEntityNotFound() {
        when(userStorage.findById(any())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.getUserDto(user.getId()));
    }

    @Test
    void findAllUserAndThenOk() {
        when(userStorage.findAll()).thenReturn(List.of(user, anotherUser));

        List<UserDto> dtoList = service.getAllUsers();

        assertThat(dtoList.size(), equalTo(2));
        assertThat(dtoList.get(0).getName(), equalTo(user.getName()));
        assertThat(dtoList.get(0).getEmail(), equalTo(user.getEmail()));
        assertThat(dtoList.get(1).getName(), equalTo(anotherUser.getName()));
        assertThat(dtoList.get(1).getEmail(), equalTo(anotherUser.getEmail()));
    }

    @Test
    void findAllUserWithEmptyDbAndThenOk() {
        when(userStorage.findAll()).thenReturn(Collections.emptyList());

        List<UserDto> dtoList = service.getAllUsers();

        assertThat(dtoList.size(), equalTo(0));
    }
}
