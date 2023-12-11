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

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.constants.headers.Headers.USER_ID;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    private BookingClient client;
    @Autowired
    private MockMvc mvc;


    BookingRequestDto bookingRequestDto;
    private final LocalDateTime start = LocalDateTime.of(2050, 8, 29,
            0, 0, 15);
    private final LocalDateTime end = LocalDateTime.of(2030, 8, 30,
            0, 0, 15); //bad date

    @BeforeEach
    void createBookingControllerEnvironment() {
        bookingRequestDto = new BookingRequestDto(1L, 1L, start, end);
    }

    @Test
    void addNewBookingAndThenThrowsNotAvailable() throws Exception {
        mvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
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
                .andExpect(status().isBadRequest());
    }
}
