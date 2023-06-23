package ru.practicum.shareit.json;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.user.dto.UserShort;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoJsonTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void testUserDto() throws Exception {
        LocalDateTime nowTime = LocalDateTime.now();
        final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        BookingDto bookingDto = new BookingDto(
                1L,
                BookingStatus.WAITING,
                nowTime.plusHours(1),
                nowTime.plusHours(2),
                null,
                new UserShort(1L)
        );

        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(nowTime.plusHours(1).format(formatter));
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(nowTime.plusHours(2).format(formatter));
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
        assertThat(result).extractingJsonPathStringValue("$.items").isEqualTo(null);
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
    }
}