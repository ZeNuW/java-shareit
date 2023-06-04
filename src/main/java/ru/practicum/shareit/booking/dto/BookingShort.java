package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public interface BookingShort {
    Long getId();

    @JsonProperty("bookerId")
    Long getBooker();

    @JsonIgnore
    LocalDateTime getEndOfBooking();

    @JsonIgnore
    LocalDateTime getStartOfBooking();

    @JsonIgnore
    Long getItem();
}