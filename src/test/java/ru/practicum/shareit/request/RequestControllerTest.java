package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.request.dto.RequestItemRequestDto;
import ru.practicum.shareit.request.dto.RequestItemResponseDto;
import ru.practicum.shareit.request.dto.RequestItemShortResponseDto;
import ru.practicum.shareit.request.service.RequestItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.constants.headers.HeadersConstants.USER_ID;

@WebMvcTest(controllers = ItemRequestController.class)
class RequestControllerTest {

    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    private RequestItemService requestItemService;
    @Autowired
    private MockMvc mvc;

    private RequestItemRequestDto requestDto;
    private ItemForRequestDto itemForRequestDto;
    private RequestItemResponseDto requestItemResponseDto;
    private RequestItemShortResponseDto requestItemShortResponseDto;
    private final LocalDateTime created = LocalDateTime.of(2050, 8, 29,
            0, 0, 15);

    @BeforeEach
    void createItemControllerEnvironment() {
        requestDto = new RequestItemRequestDto("need item");
        itemForRequestDto = new ItemForRequestDto(1L, "item", "classic item", true, 1L);
        requestItemShortResponseDto = new RequestItemShortResponseDto(1L, "need item", created);
        requestItemResponseDto = new RequestItemResponseDto(1L, "need item", created, List.of(itemForRequestDto));
    }

    @Test
    void createRequestAndThenStatusOk() throws Exception {
        when(requestItemService.addRequest(anyLong(), any())).thenReturn(requestItemShortResponseDto);

        mvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestItemShortResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(String.valueOf(requestItemShortResponseDto.getDescription()))))
                .andExpect((jsonPath("$.created", is(String.valueOf(requestItemShortResponseDto.getCreated())))));
    }

    @Test
    void createRequestAndThenTrowEntityNotFound() throws Exception {
        when(requestItemService.addRequest(anyLong(), any())).thenThrow(EntityNotFoundException.class);

        mvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnUserRequestAndThenStatusIsOk() throws Exception {
        when(requestItemService.getMyRequests(anyLong())).thenReturn(List.of(requestItemResponseDto));

        mvc.perform(get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void shouldReturnAllUserRequestAndThenStatusIsOk() throws Exception {
        when(requestItemService.getAllUserRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(requestItemResponseDto));

        mvc.perform(get("/requests/all")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .param("from", "0")
                        .param("size", "1")
                        .header(USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void returnAllUsersRequestAndThenThrowsValid() throws Exception {
        when(requestItemService.getAllUserRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(requestItemResponseDto));

        mvc.perform(get("/requests/all")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .param("from", "-1") // bad param
                        .param("size", "1")
                        .header(USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void returnRequestByIdAndThenStatusIsOk() throws Exception {
        when(requestItemService.getRequestItemResponseDto(anyLong(), anyLong())).thenReturn(requestItemResponseDto);

        mvc.perform(get("/requests/{requestId}", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestItemShortResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(String.valueOf(requestItemShortResponseDto.getDescription()))))
                .andExpect((jsonPath("$.created", is(String.valueOf(requestItemShortResponseDto.getCreated())))));
    }
}
