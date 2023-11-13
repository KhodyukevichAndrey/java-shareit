package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.validator.RentDate;

import javax.validation.GroupSequence;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@RentDate(groups = {BookingRequestDto.RentDateValidation.class})
@GroupSequence({BookingRequestDto.class, BookingRequestDto.RentDateValidation.class})
public class BookingRequestDto {
    private long id;
    @NotNull
    private long itemId;
    @FutureOrPresent
    @NotNull
    private LocalDateTime start;
    @FutureOrPresent
    @NotNull
    private LocalDateTime end;

    public interface RentDateValidation {
    }
}
