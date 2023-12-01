package ru.practicum.ewm.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.Pagination;
import ru.practicum.ewm.comment.dao.CommentRepository;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.NewCommentDto;
import ru.practicum.ewm.comment.dto.UpdateCommentRequestDto;
import ru.practicum.ewm.comment.mapper.CommentMapper;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.event.dao.EventRepository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.exception.exceptions.EventNotPublishedException;
import ru.practicum.ewm.exception.exceptions.ObjectNotFoundException;
import ru.practicum.ewm.exception.exceptions.UnableToUpdateCommentException;
import ru.practicum.ewm.exception.exceptions.UserIsNotCommentAuthorException;
import ru.practicum.ewm.user.dao.UserRepository;
import ru.practicum.ewm.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CommentRepository commentRepository;

    public List<CommentDto> getComments(Long userId, Integer from, Integer size) {
        Pageable page = Pagination.createPageTemplate(from, size);
        userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("User with id=" + userId + " doesn't exist."));
        return commentRepository.findAllByCommentatorIdOrderByCreatedOn(userId, page)
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    public CommentDto getComment(Long userId, Long commentId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("User with id=" + userId + " doesn't exist."));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ObjectNotFoundException("Comment with id=" + commentId + " doesn't exist."));
        if (!comment.getCommentator().getId().equals(userId))
            throw new UserIsNotCommentAuthorException("User with id=" + userId + " is not an author of comment with id=" + commentId);
        return CommentMapper.toCommentDto(comment);
    }

    public CommentDto postComment(Long userId, NewCommentDto newCommentDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("User with id=" + userId + " doesn't exist."));
        Event event = eventRepository.findById(newCommentDto.getEvent())
                .orElseThrow(() -> new ObjectNotFoundException("Event with id=" + newCommentDto.getEvent() + " doesn't exist."));
        if (event.getState() != EventState.PUBLISHED)
            throw new EventNotPublishedException("Cannot comment not published event.");
        return CommentMapper.toCommentDto(commentRepository.save(CommentMapper.toComment(newCommentDto, user, event)));
    }

    public CommentDto patchComment(Long userId, Long commentId, UpdateCommentRequestDto updateCommentRequestDto) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("User with id=" + userId + " doesn't exist."));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ObjectNotFoundException("Comment with id=" + commentId + " doesn't exist."));
        if (!comment.getCommentator().getId().equals(userId))
            throw new UserIsNotCommentAuthorException("Cannot change comment by not comment's author.");
        comment.setText(updateCommentRequestDto.getText());
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    public void deleteComment(Long userId, Long commentId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("User with id=" + userId + " doesn't exist."));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ObjectNotFoundException("Comment with id=" + commentId + " doesn't exist."));
        if (!(comment.getCommentator().getId().equals(userId) || comment.getEvent().getInitiator().getId().equals(userId)))
            throw new UnableToUpdateCommentException("Cannot delete a comment by not event initiator or commentator.");
        commentRepository.deleteById(commentId);
    }
}
