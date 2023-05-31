package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface ItemService {

    ItemDto addItem(long userId, ItemDto itemDto);

    ItemDto updateItem(long userId, ItemDto itemDto, long itemId);

    ItemDto getItem(long itemId, long userId);

    List<ItemDto> getUserItems(long userId);

    List<ItemDto> searchItems(String text);

    CommentDto addComment(Long userId, Long itemId, Comment comment);
}