package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Component
public class CommentMapper {
    public CommentDto commentToDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getItem().getId(),
                comment.getAuthor().getName(),
                comment.getCreated());
    }

    public Comment dtoToComment(CommentDto dto, Item item, User author) {
        return new Comment(
                dto.getId(),
                dto.getText(),
                item,
                author,
                dto.getCreated());
    }
}
