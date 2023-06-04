package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class CommentMapper {
    public static CommentDto commentToDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .authorName(comment.getCommentator().getName())
                .created(comment.getCreated())
                .text(comment.getText())
                .build();
    }

    public static Comment commentFromDto(CommentDto commentDto, User commenter, Item item) {
        return Comment.builder()
                .id(commentDto.getId())
                .commentator(commenter)
                .created(commentDto.getCreated())
                .item(item)
                .text(commentDto.getText())
                .build();
    }
}