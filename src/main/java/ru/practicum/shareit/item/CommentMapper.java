package ru.practicum.shareit.item;

import ru.practicum.shareit.user.User;

public class CommentMapper {
    public static Comment toModel(CommentDto commentDto, Item item, User author) {
        return new Comment(
                commentDto.getId(),
                commentDto.getText(),
                item,
                author,
                commentDto.getCreated()
        );
    }

    public static CommentDto toDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }
}