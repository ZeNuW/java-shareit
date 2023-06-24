package ru.practicum.shareit.valid;

import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class CheckDateValidator implements ConstraintValidator<StartBeforeOrNotEqualEndDateValid, BookingDto> {
    @Override
    public void initialize(StartBeforeOrNotEqualEndDateValid constraintAnnotation) {
    }

    @Override
    public boolean isValid(BookingDto bookingDto, ConstraintValidatorContext constraintValidatorContext) {
        LocalDateTime start = bookingDto.getStartOfBooking();
        LocalDateTime end = bookingDto.getEndOfBooking();
        if (start == null || end == null) {
            return false;
        }
        return start.isBefore(end) && !start.isEqual(end);
    }
}