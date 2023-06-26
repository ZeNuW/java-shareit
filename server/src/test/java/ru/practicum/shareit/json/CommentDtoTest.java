package ru.practicum.shareit.json;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.CommentDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CommentDtoTest {

    @Autowired
    private JacksonTester<CommentDto> json;

    @Test
    void testUserDto() throws Exception {
        LocalDateTime nowTime = LocalDateTime.now();
        final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        CommentDto commentDto = new CommentDto(
                1L,
                "description",
                "Пользователь1",
                nowTime);

        JsonContent<CommentDto> result = json.write(commentDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("description");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("Пользователь1");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(nowTime.format(formatter));
    }
}