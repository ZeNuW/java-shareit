package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemShort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserShort;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {

    public static BookingDto bookingToDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .startOfBooking(booking.getStartOfBooking())
                .endOfBooking(booking.getEndOfBooking())
                .status(booking.getStatus())
                .item(new ItemShort(booking.getItem().getId(), booking.getItem().getName()))
                .booker(new UserShort(booking.getBooker()))
                .build();
    }

    public static Booking bookingFromDto(BookingDto bookingDto, Item item) {
        return Booking.builder()
                .id(bookingDto.getId())
                .startOfBooking(bookingDto.getStartOfBooking())
                .endOfBooking(bookingDto.getEndOfBooking())
                .status(bookingDto.getStatus())
                .item(item)
                .booker(bookingDto.getBooker().getId())
                .build();
    }
}