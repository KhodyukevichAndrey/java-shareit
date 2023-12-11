package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.validator.RentDate;

import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@RentDate
public class BookingRequestDto {
    private long id;
    private long itemId;
    @FutureOrPresent
    private LocalDateTime start;
    private LocalDateTime end;
}