package ru.practicum.shareit.mapper;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;

public class CommentMapperTest {

    private final EasyRandom generator = new EasyRandom();

    @Test
    public void commentToDtoTest() {
        Comment comment = generator.nextObject(Comment.class);
        CommentDto commentDto = CommentMapper.commentToDto(comment);
        assertThat(commentDto.getId()).isEqualTo(comment.getId());
        assertThat(commentDto.getText()).isEqualTo(comment.getText());
        assertThat(commentDto.getCreated()).isEqualTo(comment.getCreated());
        assertThat(commentDto.getAuthorName()).isEqualTo(comment.getCommentator().getName());

    }

    @Test
    public void commentFromDtoTest() {
        CommentDto commentDto = generator.nextObject(CommentDto.class);
        Item item = generator.nextObject(Item.class);
        User user = generator.nextObject(User.class);
        user.setName(commentDto.getAuthorName());
        Comment comment = CommentMapper.commentFromDto(commentDto, user, item);
        assertThat(comment.getId()).isEqualTo(commentDto.getId());
        assertThat(comment.getText()).isEqualTo(commentDto.getText());
        assertThat(comment.getCreated()).isEqualTo(commentDto.getCreated());
        assertThat(comment.getCommentator().getName()).isEqualTo(commentDto.getAuthorName());
    }
}
