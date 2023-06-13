package ru.practicum.shareit.request.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemRequestMapper {
    public static ItemRequest itemRequestDtoToItemRequest(ItemRequestDto itemRequestDto, User creator) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(itemRequestDto.getId());
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setCreator(creator);
        itemRequest.setItems(new ArrayList<>());
        if (itemRequestDto.getCreated() == null) {
            itemRequest.setCreated(LocalDateTime.now());
        } else {
            itemRequest.setCreated(itemRequestDto.getCreated());
        }
        return itemRequest;
    }

    public static ItemRequestDto itemRequestToItemRequestDto(ItemRequest itemRequest) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(itemRequest.getId());
        itemRequestDto.setDescription(itemRequest.getDescription());
        itemRequestDto.setCreated(itemRequest.getCreated());
        itemRequestDto.setItems(ItemMapper.itemToDto(itemRequest.getItems()));
        return itemRequestDto;
    }

    public static List<ItemRequestDto> itemRequestToItemRequestDto(Iterable<ItemRequest> itemRequests) {
        List<ItemRequestDto> dtos = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            dtos.add(itemRequestToItemRequestDto(itemRequest));
        }
        return dtos;
    }
}