package ru.practicum.shareit.mapper;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ItemRequestMapperTest {

    private final EasyRandom generator = new EasyRandom();

    @Test
    public void itemRequestDtoToItemRequestTest() {
        ItemRequestDto itemRequestDto = generator.nextObject(ItemRequestDto.class);
        User user = generator.nextObject(User.class);
        ItemRequest itemRequest = ItemRequestMapper.itemRequestDtoToItemRequest(itemRequestDto, user);
        assertThat(itemRequest.getId()).isEqualTo(itemRequestDto.getId());
        assertThat(itemRequest.getDescription()).isEqualTo(itemRequestDto.getDescription());
        assertThat(itemRequest.getCreated()).isEqualTo(itemRequestDto.getCreated());
    }

    @Test
    public void itemRequestToItemRequestDtoTest() {
        ItemRequest itemRequest = generator.nextObject(ItemRequest.class);
        ItemDto item = generator.nextObject(ItemDto.class);
        item.setRequestId(itemRequest.getId());
        ItemRequestDto itemRequestDto = ItemRequestMapper.itemRequestToItemRequestDto(itemRequest, List.of(item));
        assertThat(itemRequestDto.getId()).isEqualTo(itemRequest.getId());
        assertThat(itemRequestDto.getDescription()).isEqualTo(itemRequest.getDescription());
        assertThat(itemRequestDto.getCreated()).isEqualTo(itemRequest.getCreated());
    }

    @Test
    public void itemRequestToItemRequestDtoIterableTest() {
        ItemRequest itemRequest = generator.nextObject(ItemRequest.class);
        ItemRequest itemRequest2 = generator.nextObject(ItemRequest.class);
        ItemRequest itemRequest3 = generator.nextObject(ItemRequest.class);
        ItemDto item = generator.nextObject(ItemDto.class);
        item.setRequestId(itemRequest.getId());
        List<ItemRequestDto> itemRequestDto = ItemRequestMapper.itemRequestToItemRequestDto(List.of(itemRequest,itemRequest2, itemRequest3), List.of(item));
        assertThat(itemRequest.getId()).isEqualTo(itemRequestDto.get(0).getId());
        assertThat(itemRequest.getDescription()).isEqualTo(itemRequestDto.get(0).getDescription());
        assertThat(itemRequest.getCreated()).isEqualTo(itemRequestDto.get(0).getCreated());
        assertThat(itemRequest2.getId()).isEqualTo(itemRequestDto.get(1).getId());
        assertThat(itemRequest2.getDescription()).isEqualTo(itemRequestDto.get(1).getDescription());
        assertThat(itemRequest2.getCreated()).isEqualTo(itemRequestDto.get(1).getCreated());
        assertThat(itemRequest3.getId()).isEqualTo(itemRequestDto.get(2).getId());
        assertThat(itemRequest3.getDescription()).isEqualTo(itemRequestDto.get(2).getDescription());
        assertThat(itemRequest3.getCreated()).isEqualTo(itemRequestDto.get(2).getCreated());
    }
}
