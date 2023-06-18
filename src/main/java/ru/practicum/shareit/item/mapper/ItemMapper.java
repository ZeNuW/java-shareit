package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookings;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ItemMapper {

    public static ItemDto itemToDto(Item item) {
        Long requestId = null;
        if (item.getItemRequest() != null) {
            requestId = item.getItemRequest().getId();
        }
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(requestId)
                .build();
    }

    public static List<ItemDto> itemToDto(Iterable<Item> items) {
        List<ItemDto> dtos = new ArrayList<>();
        for (Item item : items) {
            dtos.add(itemToDto(item));
        }
        return dtos;
    }

    public static Item itemFromDto(ItemDto itemDto, User owner) {
        ItemRequest itemRequest = null;
        if (itemDto.getRequestId() != null) {
            itemRequest = new ItemRequest();
            itemRequest.setId(itemDto.getRequestId());
        }
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(owner)
                .itemRequest(itemRequest)
                .build();
    }

    public static ItemWithBookings itemToItemWithBookings(Item item, List<CommentDto> commentsDto) {
        Long requestId = null;
        if (item.getItemRequest() != null) {
            requestId = item.getItemRequest().getId();
        }
        return ItemWithBookings.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .comments(commentsDto)
                .requestId(requestId)
                .build();
    }

    public static List<ItemWithBookings> itemToItemWithBookings(Iterable<Item> items, List<Comment> comments) {
        List<ItemWithBookings> dtos = new ArrayList<>();
        for (Item item : items) {
            List<Comment> itemComments = comments.stream().filter(comment -> comment.getItem().getId().equals(item.getId())).collect(Collectors.toList());
            dtos.add(itemToItemWithBookings(item, CommentMapper.commentToDto(itemComments)));
        }
        return dtos;
    }
}