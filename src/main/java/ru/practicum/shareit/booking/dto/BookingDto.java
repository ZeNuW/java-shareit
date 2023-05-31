package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemShort;
import ru.practicum.shareit.user.dto.UserShort;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class BookingDto {
    private Long id;
    private BookingStatus status;
    @JsonProperty(value = "start")
    @NotNull
    private LocalDateTime startOfBooking;
    @JsonProperty(value = "end")
    @NotNull
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
}