package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface BookingShort {
    Long getId();

    @JsonProperty("bookerId")
    Long getBooker();
}