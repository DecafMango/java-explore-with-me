package ru.practicum.ewm.participation_request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.dao.EventRepository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.exception.exceptions.*;
import ru.practicum.ewm.participation_request.dao.ParticipationRequestRepository;
import ru.practicum.ewm.participation_request.dto.ParticipationRequestDto;
import ru.practicum.ewm.participation_request.mapper.ParticipationRequestMapper;
import ru.practicum.ewm.participation_request.model.ParticipationRequest;
import ru.practicum.ewm.participation_request.model.ParticipationRequestStatus;
import ru.practicum.ewm.user.dao.UserRepository;
import ru.practicum.ewm.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParticipationRequestService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final ParticipationRequestRepository participationRequestRepository;

    public List<ParticipationRequestDto> getUserParticipationRequests(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("User with id=" + userId + " doesn't exist."));
        return participationRequestRepository.findAllByRequesterId(userId)
                .stream()
                .map(ParticipationRequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ParticipationRequestDto postParticipationRequest(Long userId, Long eventId) {
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("User with id=" + userId + " doesn't exist."));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ObjectNotFoundException("Event with id=" + eventId + " doesn't exist."));

        if (participationRequestRepository.findByRequesterIdAndEventId(userId, eventId).isPresent())
            throw new ParticipationRequestExistException("Participation request from user with id=" + userId + " to event with id=" + event + " is already exists.");
        if (event.getInitiator().getId().equals(userId))
            throw new UserIsEventInitiatorException("Cannot create participation request from event initiator.");
        if (event.getState() != EventState.PUBLISHED)
            throw new EventNotPublishedException("Cannot create participation request to not published event.");
        if (event.getParticipantLimit() != 0 && event.getParticipantLimit().equals(event.getConfirmedRequests()))
            throw new ParticipantLimitException("Cannot create participation request to event with participants limit reached.");

        ParticipationRequest participationRequest = new ParticipationRequest(null, LocalDateTime.now(), requester, event, ParticipationRequestStatus.PENDING);

        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            participationRequest.setStatus(ParticipationRequestStatus.CONFIRMED);
        }

        eventRepository.save(event);
        return ParticipationRequestMapper.toParticipationRequestDto(participationRequestRepository.save(participationRequest));
    }

    @Transactional
    public ParticipationRequestDto cancelParticipationRequest(Long userId, Long requestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("User with id=" + userId + " doesn't exist."));
        ParticipationRequest request = participationRequestRepository.findById(requestId)
                .orElseThrow(() -> new ObjectNotFoundException("Participation request with id=" + requestId + " doesn't exist."));
        if (!request.getRequester().getId().equals(userId))
            throw new NotRequesterOfParticipationRequestException("Cannot cancel participation request from not requester.");
        if (request.getStatus() == ParticipationRequestStatus.CONFIRMED) {
            Event event = request.getEvent();
            event.setConfirmedRequests(event.getConfirmedRequests() - 1);
            eventRepository.save(event);
        }
        request.setStatus(ParticipationRequestStatus.CANCELED);
        return ParticipationRequestMapper.toParticipationRequestDto(participationRequestRepository.save(request));
    }
}
