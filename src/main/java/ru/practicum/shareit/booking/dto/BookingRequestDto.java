package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.validator.RentDate;

import javax.validation.constraints.Future;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@RentDate
public class BookingRequestDto {
    private long id;
    private long itemId;
    @Future
    private LocalDateTime start;
    private LocalDateTime end;

    public interface RentDateValidation {
    }
}
