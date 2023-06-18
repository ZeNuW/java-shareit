package ru.practicum.shareit.request.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemRequestMapper {
    public static ItemRequest itemRequestDtoToItemRequest(ItemRequestDto itemRequestDto, User creator) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(itemRequestDto.getId());
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setCreator(creator);
        if (itemRequestDto.getCreated() == null) {
            itemRequest.setCreated(LocalDateTime.now());
        } else {
            itemRequest.setCreated(itemRequestDto.getCreated());
        }
        return itemRequest;
    }

    public static ItemRequestDto itemRequestToItemRequestDto(ItemRequest itemRequest, List<ItemDto> items) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(itemRequest.getId());
        itemRequestDto.setDescription(itemRequest.getDescription());
        itemRequestDto.setCreated(itemRequest.getCreated());
        itemRequestDto.setItems(items);
        return itemRequestDto;
    }

    public static List<ItemRequestDto> itemRequestToItemRequestDto(Iterable<ItemRequest> itemRequests, List<ItemDto> items) {
        List<ItemRequestDto> dtos = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            List<ItemDto> requestItems = items.stream()
                    .filter(itemDto -> itemDto.getRequestId().equals(itemRequest.getId())).collect(Collectors.toList());
            dtos.add(itemRequestToItemRequestDto(itemRequest, requestItems));
        }
        return dtos;
    }
}