package ru.practicum.ewm.participation_request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.participation_request.service.ParticipationRequestService;
import ru.practicum.ewm.participation_request.dto.ParticipationRequestDto;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
@Slf4j
public class PrivateParticipationRequestController {

    private final ParticipationRequestService service;

    @GetMapping
    public List<ParticipationRequestDto> getParticipationRequests(@PathVariable Long userId) {
        log.info("Getting user's with id={} participation requests.", userId);
        return service.getUserParticipationRequests(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto postParticipationRequest(@PathVariable Long userId,
                                                                @RequestParam Long eventId) {
        log.info("Posting participation request to event with id={} from user with id={}", eventId, userId);
        return service.postParticipationRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelParticipationRequest(@PathVariable Long userId,
                                                              @PathVariable Long requestId) {
        log.info("Canceling participation request with id={} from user with id={}", requestId, userId);
        return service.cancelParticipationRequest(userId, requestId);
    }

}
