package ru.practicum.shareit.user.storage;


import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {

    User addUser(User user);

    User updateUser(long userId, User user);

    Optional<User> getUser(long id);

    List<User> getAllUsers();

    void deleteUser(long id);
}
