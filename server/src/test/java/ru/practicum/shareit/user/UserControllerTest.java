package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    private UserService userService;
    @Autowired
    private MockMvc mvc;

    private UserDto userDto;

    @BeforeEach
    void createItemControllerEnvironment() {
        userDto = new UserDto(1L, "name", "email@mail.com");
    }

    @Test
    void createUserAndThenStatusOk() throws Exception {
        when(userService.addUser(any())).thenReturn(userDto);

        mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(String.valueOf(userDto.getName()))))
                .andExpect((jsonPath("$.email", is(String.valueOf(userDto.getEmail())))));
    }

    @Test
    void updateUserAndThenStatusOk() throws Exception {
        when(userService.updateUser(anyLong(), any())).thenReturn(userDto);

        mvc.perform(patch("/users/{userId}", 1L)
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(String.valueOf(userDto.getName()))))
                .andExpect((jsonPath("$.email", is(String.valueOf(userDto.getEmail())))));
    }

    @Test
    void findUserAndThenStatusOk() throws Exception {
        when(userService.getUserDto(anyLong())).thenReturn(userDto);

        mvc.perform(get("/users/{userId}", 1L)
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(String.valueOf(userDto.getName()))))
                .andExpect((jsonPath("$.email", is(String.valueOf(userDto.getEmail())))));
    }

    @Test
    void findAllUsersAndThenStatusOk() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(userDto));

        mvc.perform(get("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void deleteUserAndThenStatusOk() throws Exception {
        mvc.perform(delete("/users/{userId}", 1L)
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void deleteUserWithWrongIdAndThenThrowsEntityNotFound() throws Exception {
        doThrow(new EntityNotFoundException("EntityNotFound")).when(userService).deleteUser(anyLong());

        mvc.perform(delete("/users/{userId}", 1L)
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

}
