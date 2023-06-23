package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto createItemRequest(ItemRequestDto itemRequestDto, Long userId);

    List<ItemRequestDto> getItemRequestByOwner(Long userId);

    List<ItemRequestDto> getAllItemRequestByPage(Integer from, Integer size, Long userId);

    ItemRequestDto getItemRequest(Long userId, Long requestId);
}