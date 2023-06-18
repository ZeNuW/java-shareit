package ru.practicum.shareit.mapper;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

import static org.assertj.core.api.Assertions.assertThat;

public class BookingMapperTest {

    private final EasyRandom generator = new EasyRandom();

    @Test
    public void bookingToDtoTest() {
        Booking booking = generator.nextObject(Booking.class);
        BookingDto bookingDto = BookingMapper.bookingToDto(booking);
        assertThat(bookingDto.getId()).isEqualTo(booking.getId());
        assertThat(bookingDto.getStartOfBooking()).isEqualTo(booking.getStartOfBooking());
        assertThat(bookingDto.getEndOfBooking()).isEqualTo(booking.getEndOfBooking());
        assertThat(bookingDto.getStatus()).isEqualTo(booking.getStatus());
        assertThat(bookingDto.getBooker().getId()).isEqualTo(booking.getBooker());
        assertThat(bookingDto.getItem().getId()).isEqualTo(booking.getItem().getId());
    }

    @Test
    public void bookingFromDtoTest() {
        BookingDto bookingDto = generator.nextObject(BookingDto.class);
        Item item = generator.nextObject(Item.class);
        bookingDto.setItemId(item.getId());
        Booking booking = BookingMapper.bookingFromDto(bookingDto,item);
        assertThat(bookingDto.getId()).isEqualTo(booking.getId());
        assertThat(bookingDto.getStartOfBooking()).isEqualTo(booking.getStartOfBooking());
        assertThat(bookingDto.getEndOfBooking()).isEqualTo(booking.getEndOfBooking());
        assertThat(bookingDto.getStatus()).isEqualTo(booking.getStatus());
        assertThat(bookingDto.getBooker().getId()).isEqualTo(booking.getBooker());
        assertThat(bookingDto.getItem().getId()).isEqualTo(booking.getItem().getId());
    }
}
