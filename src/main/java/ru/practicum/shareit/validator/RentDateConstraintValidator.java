package ru.practicum.shareit.validator;

import ru.practicum.shareit.booking.dto.BookingRequestDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class RentDateConstraintValidator implements ConstraintValidator<RentDate, BookingRequestDto> {

    @Override
    public boolean isValid(BookingRequestDto bookingRequestDto, ConstraintValidatorContext cxt) {
        LocalDateTime start = bookingRequestDto.getStart();
        LocalDateTime end = bookingRequestDto.getEnd();
        if (start == null || end == null) {
            return false;
        }
        return !start.isAfter(end) && !start.isEqual(end);
    }
}
