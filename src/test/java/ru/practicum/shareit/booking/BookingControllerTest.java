package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.user.dto.UserShortDto;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.shareit.constants.headers.HeadersConstants.USER_ID;


@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    private BookingService service;
    @Autowired
    private MockMvc mvc;


    UserShortDto userShortDto;
    ItemShortDto itemShortDto;
    BookingRequestDto bookingRequestDto;
    BookingResponseDto bookingResponseDto;
    BookingShortDto bookingShortDto;
    private final LocalDateTime start = LocalDateTime.of(2050, 8, 29,
            0, 0, 15);
    private final LocalDateTime end = LocalDateTime.of(2050, 8, 30,
            0, 0, 15);

    @BeforeEach
    void createBookingControllerEnvironment() {
        userShortDto = new UserShortDto(1L);
        itemShortDto = new ItemShortDto(1L, "itemName");
        bookingRequestDto = new BookingRequestDto(1L, itemShortDto.getId(), start, end);
        bookingResponseDto = new BookingResponseDto(1L, bookingRequestDto.getStart(),
                bookingRequestDto.getEnd(), BookingStatus.WAITING, userShortDto, itemShortDto);
        bookingShortDto = new BookingShortDto(bookingResponseDto.getId(), userShortDto.getId());
    }

    @Test
    void addNewBookingAndThenStatusOk() throws Exception {
        when(service.createBooking(any(), anyLong())).thenReturn(bookingResponseDto);

        mvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(String.valueOf(bookingResponseDto.getStart()))))
                .andExpect(jsonPath("$.end", is(String.valueOf(bookingResponseDto.getEnd()))))
                .andExpect((jsonPath("$.status", is(String.valueOf(bookingResponseDto.getStatus())))));
    }

    @Test
    void addNewBookingAndThenThrowsNotAvailable() throws Exception {
        when(service.createBooking(any(), anyLong())).thenThrow(NotAvailableException.class);

        mvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateConfirmAndThenStatusOk() throws Exception {
        when(service.confirmBooking(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingResponseDto);

        mvc.perform(patch("/bookings/{bookingId}", 1L)
                        .content(objectMapper.writeValueAsString(bookingRequestDto))
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(String.valueOf(bookingResponseDto.getStart()))))
                .andExpect(jsonPath("$.end", is(String.valueOf(bookingResponseDto.getEnd()))))
                .andExpect((jsonPath("$.status", is(String.valueOf(bookingResponseDto.getStatus())))));
    }

    @Test
    void getBookingByIdAndThenStatusOk() throws Exception {
        when(service.getBookingResponse(anyLong(), anyLong())).thenReturn(bookingResponseDto);

        mvc.perform(get("/bookings/{bookingId}", 1L)
                        .content(objectMapper.writeValueAsString(bookingRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(String.valueOf(bookingResponseDto.getStart()))))
                .andExpect(jsonPath("$.end", is(String.valueOf(bookingResponseDto.getEnd()))))
                .andExpect((jsonPath("$.status", is(String.valueOf(bookingResponseDto.getStatus())))));
    }

    @Test
    void getAllUserBookingAndThenStatusIsOk() throws Exception {
        when(service.getAllUserBooking(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingResponseDto));

        mvc.perform(get("/bookings")
                        .param("state", "FUTURE")
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
    void getAllUserBookingAndThenThrow() throws Exception {
        mvc.perform(get("/bookings")
                        .param("state", "FUTURE")
                        .param("from", "-1") // bad param
                        .param("size", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getAllOwnerBookingAndThenStatusIsOk() throws Exception {
        when(service.getAllOwnerBooking(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingResponseDto));

        mvc.perform(get("/bookings/owner")
                        .param("state", "FUTURE")
                        .param("from", "0")
                        .param("size", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
}
