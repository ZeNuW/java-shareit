package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.BookingShort;

import java.util.List;

@Builder
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ItemWithBookings {
    private Long id;
    private String description;
    private String name;
    private Boolean available;
    private Long requestId;
    private BookingShort lastBooking;
    private BookingShort nextBooking;
    private List<CommentDto> comments;
}