package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.RequestItemRequestDto;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.constants.headers.Headers.USER_ID;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    private ItemRequestClient client;
    @Autowired
    private MockMvc mvc;

    private RequestItemRequestDto requestDto;

    @BeforeEach
    void createTestEnvironment() {
        byte[] array = new byte[300];
        new Random().nextBytes(array);
        String generatedString = new String(array, Charset.forName("UTF-8"));
        requestDto = new RequestItemRequestDto(generatedString);
    }

    @Test
    void getAllRequestsAndThenGetBadRequest() throws Exception {
        mvc.perform(get("/requests/all")
                        .param("from", "-1") // bad param
                        .param("size", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createItemRequestWithBadSizeDescriptionAndThenGetBadRequest() throws Exception {
        mvc.perform(post("/requests", 1L)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .header(USER_ID, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
