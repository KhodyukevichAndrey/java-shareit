package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto addUser(@Validated(UserDto.Create.class) @RequestBody UserDto userDto) {
        log.debug("Получен запрос POST /users");
        return userService.addUser(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable long userId,
                              @Validated(UserDto.Update.class) @RequestBody UserDto userDto) {
        log.debug("Получен запрос PUT /users/{userId}");
        return userService.updateUser(userId, userDto);
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable Long userId) {
        log.debug("Получен запрос GET /users/{userId}");
        return userService.getUserDto(userId);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.debug("Получен запрос GET /users");
        return userService.getAllUsers();
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        log.debug("Получен запрос DELETE /users");
        userService.deleteUser(userId);
    }
}
