package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.participation_request.dto.ParticipationRequestDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Slf4j
public class PrivateEventController {

    private final EventService service;

    @GetMapping
    public List<EventShortDto> getUserEvents(@PathVariable Long userId,
                                             @RequestParam(required = false, defaultValue = "0") Integer from,
                                             @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("Getting events from user with id={}, from={}, size={}", userId, from, size);
        return service.getUserEvents(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getUserEvent(@PathVariable Long userId,
                                     @PathVariable Long eventId) {
        log.info("Getting event with id={} from user with id={}", eventId, userId);
        return service.getUserEvent(userId, eventId);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getEventRequests(@PathVariable Long userId,
                                                          @PathVariable Long eventId) {
        log.info("Getting participation requests to event with id={} from user with id={}", eventId, userId);
        return service.getEventRequests(userId, eventId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto postEvent(@PathVariable(name = "userId") Long initiatorId,
                                  @Valid @RequestBody NewEventDto newEventDto) {
        log.info("Posting by user with id={} event: {}", initiatorId, newEventDto);
        return service.postEvent(initiatorId, newEventDto);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto patchEvent(@PathVariable Long userId,
                                   @PathVariable Long eventId,
                                   @Valid @RequestBody UpdateEventUserRequest updateEventUserRequest) {
        log.info("Patching event with id={} by user with id={} followed: {}", eventId, userId, updateEventUserRequest);
        return service.patchEvent(userId, eventId, updateEventUserRequest);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult patchRequests(@PathVariable Long userId,
                                                        @PathVariable Long eventId,
                                                        @RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        log.info("Patching participation requests with id={} to status={} to event with id={} from user with id={}",
                eventRequestStatusUpdateRequest.getRequestIds(), eventRequestStatusUpdateRequest.getStatus(), eventId, userId);
        return service.patchRequests(userId, eventId, eventRequestStatusUpdateRequest);
    }

}