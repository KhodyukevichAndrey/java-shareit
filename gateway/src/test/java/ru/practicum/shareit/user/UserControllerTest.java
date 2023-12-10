package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    private UserClient client;
    @Autowired
    private MockMvc mvc;

    private UserDto userDto;
    private UserDto userDto2;

    @BeforeEach
    void createTestEnvironment() {
        byte[] array = new byte[300];
        new Random().nextBytes(array);
        String generatedString = new String(array, Charset.forName("UTF-8"));
        userDto = new UserDto(1L, generatedString, "yandex@mail.ru");
        userDto2 = new UserDto(1L, "name", "BadEmailFormat");
    }

    @Test
    void createUserWithBadNameAndThenGetBadRequest() throws Exception {
        mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUserWithBadEmailFormatAndThenGetBadRequest() throws Exception {
        mvc.perform(patch("/users/{userId}", 1L)
                        .content(objectMapper.writeValueAsString(userDto2))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
