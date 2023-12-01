package ru.practicum.ewm.comment.mapper;

import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.NewCommentDto;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.model.User;

import java.time.LocalDateTime;

public class CommentMapper {

    public static Comment toComment(NewCommentDto newCommentDto, User commentator, Event event) {
        return new Comment()
                .toBuilder()
                .id(null)
                .text(newCommentDto.getText())
                .createdOn(LocalDateTime.now())
                .commentator(commentator)
                .event(event)
                .build();
    }

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto()
                .toBuilder()
                .id(comment.getId())
                .text(comment.getText())
                .createdOn(comment.getCreatedOn())
                .commentator(UserMapper.toUserShortDto(comment.getCommentator()))
                .build();
    }
}
