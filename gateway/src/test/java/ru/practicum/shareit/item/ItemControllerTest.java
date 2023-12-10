package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.constants.headers.Headers.USER_ID;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    private ItemClient client;
    @Autowired
    private MockMvc mvc;

    private ItemDto itemDto;
    private CommentRequestDto comment;

    @BeforeEach
    void createTestEnvironment() {
        itemDto = new ItemDto(1L, "", "description", true, 1L);
        byte[] array = new byte[300];
        new Random().nextBytes(array);
        String generatedString = new String(array, Charset.forName("UTF-8"));
        comment = new CommentRequestDto(generatedString);
    }

    @Test
    void getAllOwnerItemsAndThenThrowValid() throws Exception {
        mvc.perform(get("/items")
                        .param("from", "-1") // bad param
                        .param("size", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createItemWithNoNameAndThenGetBadRequest() throws Exception {
        mvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createCommentWithBadSizeTextAndThenGetBadRequest() throws Exception {
        mvc.perform(post("/items/{itemId}/comment", 1L)
                        .content(objectMapper.writeValueAsString(comment))
                        .header(USER_ID, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
