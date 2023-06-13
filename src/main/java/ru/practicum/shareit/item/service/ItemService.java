package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookings;

import java.util.List;

public interface ItemService {

    ItemDto addItem(long userId, ItemDto itemDto);

    ItemDto updateItem(long userId, ItemDto itemDto, long itemId);

    ItemWithBookings getItem(long itemId, long userId);

    List<ItemWithBookings> getUserItems(long userId, int from, int size);

    List<ItemDto> searchItems(String text, int from, int size);

    CommentDto addComment(Long userId, Long itemId, CommentDto commentDto);
}