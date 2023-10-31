package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;
    private final UserMapper userMapper;
    private static final String WRONG_USER_ID = "Пользователь с указанным ID не найден";
    private static final String EMAIL_DUPLICATE_ERROR = "Указанный формат почты не поддерживается";

    @Override
    public UserDto addUser(UserDto userDto) {
        checkUsersEmail(userDto);
        User user = userStorage.addUser(userMapper.makeUser(userDto));
        return userMapper.makeUserDto(user);
    }

    @Override
    public UserDto updateUser(long userId, UserDto userDto) {
        User oldUser = getUser(userId);
        userDto.setId(userId);
        checkUsersEmail(userDto);

        return userMapper.makeUserDto(userStorage.updateUser(userId, updateUserFields(userDto, oldUser)));
    }

    @Override
    public UserDto getUserDto(long id) {
        return userMapper.makeUserDto(getUser(id));
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userStorage.getAllUsers();
        return users.stream()
                .map(userMapper::makeUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(long id) {
        userStorage.deleteUser(id);
    }

    private void checkUsersEmail(UserDto userDto) {
        String userEmail = userDto.getEmail();
        if (userEmail != null) {
            Optional<String> validation = userStorage.getAllUsers().stream()
                    .filter(user -> userDto.getId() != user.getId())
                    .map(User::getEmail)
                    .filter(email -> email.equals(userEmail))
                    .findAny();
            if (validation.isPresent()) {
                throw new ValidateException(EMAIL_DUPLICATE_ERROR);
            }
        }
    }

    private User updateUserFields(UserDto userDto, User oldUser) {
        String name = userDto.getName();
        String email = userDto.getEmail();
        if (name != null && !name.isBlank()) {
            oldUser.setName(name);
        }
        if (email != null && !email.isBlank()) {
            oldUser.setEmail(email);
        }
        return oldUser;
    }

    private User getUser(long userId) {
        return userStorage.getUser(userId)
                .orElseThrow(() -> new EntityNotFoundException(WRONG_USER_ID));
    }
}
