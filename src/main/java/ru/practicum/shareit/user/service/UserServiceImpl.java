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

import static ru.practicum.shareit.constants.error.ErrorConstants.WRONG_USER_ID;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Override
    @Transactional
    public UserDto addUser(UserDto userDto) {
        User user = userStorage.save(UserMapper.makeUser(userDto));
        return UserMapper.makeUserDto(user);
    }

    @Override
    @Transactional
    public UserDto updateUser(long userId, UserDto userDto) {
        User oldUser = getUser(userId);
        userDto.setId(userId);

        return UserMapper.makeUserDto(userStorage.save(updateUserFields(userDto, oldUser)));
    }

    @Override
    public UserDto getUserDto(long id) {
        return UserMapper.makeUserDto(getUser(id));
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userStorage.findAll();
        return users.stream()
                .map(UserMapper::makeUserDto)
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
