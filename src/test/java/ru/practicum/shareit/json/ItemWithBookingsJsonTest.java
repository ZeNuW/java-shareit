package ru.practicum.shareit.json;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemWithBookings;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemWithBookingsJsonTest {

    @Autowired
    private JacksonTester<ItemWithBookings> json;

    @Test
    void testUserDto() throws Exception {
        ItemWithBookings itemDto = new ItemWithBookings(
                1L,
                "description",
                "Hammer",
                false,
                2L, null, null, null);

        JsonContent<ItemWithBookings> result = json.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Hammer");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(false);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.lastBooking").isEqualTo(null);
        assertThat(result).extractingJsonPathStringValue("$.nextBooking").isEqualTo(null);
        assertThat(result).extractingJsonPathStringValue("$.comments").isEqualTo(null);
    }
}