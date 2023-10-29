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
        userDto.setId(userId);
        checkUsersEmail(userDto);
        completeFieldsForUpdate(userDto, getUser(userId));
        User user = userStorage.updateUser(userId, userMapper.makeUser(userDto));
        return userMapper.makeUserDto(user);
    }

    @Override
    public UserDto getUser(long id) {
        User user = userStorage.getUser(id)
                .orElseThrow(() -> new EntityNotFoundException(WRONG_USER_ID));
        return userMapper.makeUserDto(user);
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

    private UserDto completeFieldsForUpdate(UserDto userDto, UserDto currentUserDto) {
        if (userDto.getName() == null) {
            userDto.setName(currentUserDto.getName());
        }
        if (userDto.getEmail() == null) {
            userDto.setEmail(currentUserDto.getEmail());
        }
        return userDto;
    }
}
