package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.constants.headers.HeadersConstants.USER_ID;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    private ItemService service;
    @Autowired
    private MockMvc mvc;

    private ItemDto itemDto;
    private ItemResponseDto itemResponseDto;
    private CommentRequestDto commentRequestDto;
    private CommentResponseDto commentResponseDto;
    private BookingShortDto firstBookingShortDto;
    private BookingShortDto secondBookingShortDto;
    private List<CommentResponseDto> comments;
    private final LocalDateTime created = LocalDateTime.of(2050, 8, 29,
            0, 0, 15);


    @BeforeEach
    void createItemControllerEnvironment() {
        itemDto = new ItemDto(1L, "item", "description", true, 2L);
        firstBookingShortDto = new BookingShortDto(1L, 2L);
        secondBookingShortDto = new BookingShortDto(1L, 3L);
        commentRequestDto = new CommentRequestDto("The Best Item!");
        commentResponseDto = new CommentResponseDto(1L, commentRequestDto.getText(), "userName", created);
        comments = List.of(commentResponseDto);
        itemResponseDto = new ItemResponseDto(itemDto, firstBookingShortDto, secondBookingShortDto, comments);
    }

    @Test
    void createItemAndThenStatusOk() throws Exception {
        when(service.addItem(anyLong(), any())).thenReturn(itemDto);

        mvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(String.valueOf(itemDto.getName()))))
                .andExpect(jsonPath("$.description", is(String.valueOf(itemDto.getDescription()))))
                .andExpect((jsonPath("$.available", is(itemDto.getAvailable()))));
    }

    @Test
    void createItemAndThenThrowsEntityNotFound() throws Exception {
        when(service.addItem(anyLong(), any())).thenThrow(EntityNotFoundException.class);

        mvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateItemAndThenStatusIsOk() throws Exception {
        when(service.updateItem(anyLong(), anyLong(), any())).thenReturn(itemDto);

        mvc.perform(patch("/items/{itemId}", 1L)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(String.valueOf(itemDto.getName()))))
                .andExpect(jsonPath("$.description", is(String.valueOf(itemDto.getDescription()))))
                .andExpect((jsonPath("$.available", is(itemDto.getAvailable()))));
    }

    @Test
    void getItemAndThenStatusIsOk() throws Exception {
        when(service.getItemDto(anyLong(), anyLong())).thenReturn(itemResponseDto);

        mvc.perform(get("/items/{itemId}", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(String.valueOf(itemDto.getName()))))
                .andExpect(jsonPath("$.description", is(String.valueOf(itemDto.getDescription()))))
                .andExpect((jsonPath("$.available", is(itemDto.getAvailable()))));
    }

    @Test
    void getAllOwnerItemsAndThenStatusOk() throws Exception {
        when(service.getAllOwnersItems(anyLong(), anyInt(), anyInt())).thenReturn(List.of(itemResponseDto));

        mvc.perform(get("/items")
                        .param("from", "0")
                        .param("size", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void deleteItemAndThenStatusOk() throws Exception {
        mvc.perform(delete("/items/{itemId}", 1L)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void searchItemAndThenStatusOk() throws Exception {
        when(service.searchItems(anyString(), anyInt(), anyInt())).thenReturn(List.of(itemResponseDto));

        mvc.perform(get("/items/search")
                        .param("text", "text")
                        .param("from", "0")
                        .param("size", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void addCommentAndThenStatusOk() throws Exception {
        when(service.addComment(any(), anyLong(), anyLong())).thenReturn(commentResponseDto);

        mvc.perform(post("/items/{itemId}/comment", 1L)
                        .content(objectMapper.writeValueAsString(commentRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(String.valueOf(commentResponseDto.getText()))))
                .andExpect(jsonPath("$.authorName", is(String.valueOf(commentResponseDto.getAuthorName()))));
    }
}
