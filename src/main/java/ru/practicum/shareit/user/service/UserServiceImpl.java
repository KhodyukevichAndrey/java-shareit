package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;
    private final UserMapper userMapper;
    private static final String WRONG_USER_ID = "Пользователь с указанным ID не найден";
    private static final String EMAIL_DUPLICATE_ERROR = "Указанный формат почты не поддерживается";

    @Override
    @Transactional
    public UserDto addUser(UserDto userDto) {
        User user = userStorage.save(userMapper.makeUser(userDto));
        return userMapper.makeUserDto(user);
    }

    @Override
    @Transactional
    public UserDto updateUser(long userId, UserDto userDto) {
        User oldUser = getUser(userId);
        userDto.setId(userId);

        return userMapper.makeUserDto(userStorage.save(updateUserFields(userDto, oldUser)));
    }

    @Override
    public UserDto getUserDto(long id) {
        return userMapper.makeUserDto(getUser(id));
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userStorage.findAll();
        return users.stream()
                .map(userMapper::makeUserDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteUser(long id) {
        userStorage.deleteById(id);
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
        return userStorage.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(WRONG_USER_ID));
    }
}
