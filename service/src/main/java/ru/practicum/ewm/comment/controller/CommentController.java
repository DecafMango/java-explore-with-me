package ru.practicum.ewm.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.NewCommentDto;
import ru.practicum.ewm.comment.dto.UpdateCommentRequestDto;
import ru.practicum.ewm.comment.service.CommentService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/comments")
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final CommentService service;

    @GetMapping
    public List<CommentDto> getUserComments(@PathVariable Long userId,
                                            @RequestParam(required = false, defaultValue = "0") Integer from,
                                            @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("Getting comments from user with id={}", userId);
        return service.getComments(userId, from, size);
    }

    @GetMapping("/{commentId}")
    public CommentDto getUserComment(@PathVariable Long userId,
                                     @PathVariable Long commentId) {
        log.info("Getting comment with id={} by user with id={}", commentId, userId);
        return service.getComment(userId, commentId);
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto postComment(@PathVariable Long userId,
                                  @Valid @RequestBody NewCommentDto newCommentDto) {
        log.info("Posting comment {} by user with id={}", newCommentDto, userId);
        return service.postComment(userId, newCommentDto);
    }

    @PatchMapping("/{commentId}")
    public CommentDto patchComment(@PathVariable Long userId,
                                   @PathVariable Long commentId,
                                   @Valid @RequestBody UpdateCommentRequestDto updateCommentRequestDto) {
        log.info("Patching comment with id={} by user with id={} followed: {}", commentId, userId, updateCommentRequestDto);
        return service.patchComment(userId, commentId, updateCommentRequestDto);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long userId,
                              @PathVariable Long commentId) {
        log.info("Deleting comment with id={} by user with id={}", commentId, userId);
        service.deleteComment(userId, commentId);
    }
}
