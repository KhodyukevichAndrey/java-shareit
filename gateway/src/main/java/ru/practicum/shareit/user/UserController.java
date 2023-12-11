package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final UserClient client;

    @PostMapping
    public ResponseEntity<Object> addUser(@Validated(UserDto.Create.class) @RequestBody UserDto userDto) {
        log.debug("Получен запрос POST /users");
        return client.addUser(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable long userId,
                                             @Validated(UserDto.Update.class) @RequestBody UserDto userDto) {
        log.debug("Получен запрос PUT /users/{userId}");
        return client.updateUser(userId, userDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUser(@PathVariable Long userId) {
        log.debug("Получен запрос GET /users/{userId}");
        return client.getUser(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.debug("Получен запрос GET /users");
        return client.getAllUsers();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long userId) {
        log.debug("Получен запрос DELETE /users");
        return client.deleteUser(userId);
    }
}
