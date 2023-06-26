package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemShort;
import ru.practicum.shareit.user.dto.UserShort;

import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@Getter
@Setter
public class BookingDto {
    private Long id;
    private BookingStatus status;
    @JsonProperty(value = "start")
    private LocalDateTime startOfBooking;
    @JsonProperty(value = "end")
    private LocalDateTime endOfBooking;
    private ItemShort item;
    private UserShort booker;

    @JsonProperty("itemId")
    public void setItemId(Long itemId) {
        if (item == null) {
            item = new ItemShort();
        }
        item.setId(itemId);
    }

    public void setBooker(Long userId) {
        if (booker == null) {
            booker = new UserShort();
        }
        booker.setId(userId);
    }
}