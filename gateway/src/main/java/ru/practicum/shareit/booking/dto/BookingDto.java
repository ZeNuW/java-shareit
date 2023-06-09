package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import ru.practicum.shareit.valid.StartBeforeOrNotEqualEndDateValid;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
@StartBeforeOrNotEqualEndDateValid
public class BookingDto {
    private Long id;
    @JsonProperty(value = "start")
    @FutureOrPresent
    private LocalDateTime startOfBooking;
    @JsonProperty(value = "end")
    private LocalDateTime endOfBooking;
    @NotNull
    private Long itemId;
}