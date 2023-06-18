package ru.practicum.shareit.mapper;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookings;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ItemMapperTest {

    private final EasyRandom generator = new EasyRandom();

    @Test
    public void itemToDtoTest() {
        Item item = generator.nextObject(Item.class);
        ItemDto itemDto = ItemMapper.itemToDto(item);
        assertThat(itemDto.getId()).isEqualTo(item.getId());
        assertThat(itemDto.getName()).isEqualTo(item.getName());
        assertThat(itemDto.getDescription()).isEqualTo(item.getDescription());
        assertThat(itemDto.getRequestId()).isEqualTo(item.getItemRequest().getId());
        assertThat(itemDto.getAvailable()).isEqualTo(item.getAvailable());
    }

    @Test
    public void itemToDtoIterableTest() {
        Item item = generator.nextObject(Item.class);
        Item item2 = generator.nextObject(Item.class);
        Item item3 = generator.nextObject(Item.class);
        List<ItemDto> itemDto = ItemMapper.itemToDto(List.of(item, item2, item3));
        assertThat(itemDto.get(0).getId()).isEqualTo(item.getId());
        assertThat(itemDto.get(0).getName()).isEqualTo(item.getName());
        assertThat(itemDto.get(0).getDescription()).isEqualTo(item.getDescription());
        assertThat(itemDto.get(0).getRequestId()).isEqualTo(item.getItemRequest().getId());
        assertThat(itemDto.get(0).getAvailable()).isEqualTo(item.getAvailable());
        assertThat(itemDto.get(1).getId()).isEqualTo(item2.getId());
        assertThat(itemDto.get(1).getName()).isEqualTo(item2.getName());
        assertThat(itemDto.get(1).getDescription()).isEqualTo(item2.getDescription());
        assertThat(itemDto.get(1).getRequestId()).isEqualTo(item2.getItemRequest().getId());
        assertThat(itemDto.get(1).getAvailable()).isEqualTo(item2.getAvailable());
        assertThat(itemDto.get(2).getId()).isEqualTo(item3.getId());
        assertThat(itemDto.get(2).getName()).isEqualTo(item3.getName());
        assertThat(itemDto.get(2).getDescription()).isEqualTo(item3.getDescription());
        assertThat(itemDto.get(2).getRequestId()).isEqualTo(item3.getItemRequest().getId());
        assertThat(itemDto.get(2).getAvailable()).isEqualTo(item3.getAvailable());
    }

    @Test
    public void itemFromDtoTest() {
        ItemDto itemDto = generator.nextObject(ItemDto.class);
        User user = generator.nextObject(User.class);
        Item item = ItemMapper.itemFromDto(itemDto, user);
        assertThat(item.getId()).isEqualTo(itemDto.getId());
        assertThat(item.getName()).isEqualTo(itemDto.getName());
        assertThat(item.getDescription()).isEqualTo(itemDto.getDescription());
        assertThat(item.getItemRequest().getId()).isEqualTo(itemDto.getRequestId());
        assertThat(item.getAvailable()).isEqualTo(itemDto.getAvailable());
    }

    @Test
    public void itemToItemWithBookingsTest() {
        Item item = generator.nextObject(Item.class);
        CommentDto comment = generator.nextObject(CommentDto.class);
        ItemWithBookings itemDto = ItemMapper.itemToItemWithBookings(item, List.of(comment));
        assertThat(itemDto.getId()).isEqualTo(item.getId());
        assertThat(itemDto.getName()).isEqualTo(item.getName());
        assertThat(itemDto.getDescription()).isEqualTo(item.getDescription());
        assertThat(itemDto.getRequestId()).isEqualTo(item.getItemRequest().getId());
        assertThat(itemDto.getAvailable()).isEqualTo(item.getAvailable());
    }
}
