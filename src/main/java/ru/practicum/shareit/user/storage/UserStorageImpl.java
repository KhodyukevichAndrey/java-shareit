package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Component
public class UserStorageImpl implements UserStorage {

    private long userId = 1;
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User addUser(User user) {
        user.setId(userId++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(long userId, User user) { //не требуется
        user.setId(userId);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> getUser(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void deleteUser(long id) {
        users.remove(id);
    }
}
