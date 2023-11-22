package ru.practicum.ewm.event.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.EndpointHitDto;
import ru.practicum.ewm.Pagination;
import ru.practicum.ewm.StatsClient;
import ru.practicum.ewm.ViewStatsDto;
import ru.practicum.ewm.category.dao.CategoryRepository;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.event.dao.EventRepository;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.mapper.LocationMapper;
import ru.practicum.ewm.event.model.*;
import ru.practicum.ewm.exception.exceptions.*;
import ru.practicum.ewm.participation_request.dao.ParticipationRequestRepository;
import ru.practicum.ewm.participation_request.dto.ParticipationRequestDto;
import ru.practicum.ewm.participation_request.mapper.ParticipationRequestMapper;
import ru.practicum.ewm.participation_request.model.ParticipationRequest;
import ru.practicum.ewm.participation_request.model.ParticipationRequestStatus;
import ru.practicum.ewm.user.dao.UserRepository;
import ru.practicum.ewm.user.model.User;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ParticipationRequestRepository participationRequestRepository;

    private final StatsClient client;

    private static final String APPLICATION_NAME = "ewm-server";

    public List<Event> getEventsById(List<Long> ids) {
        return eventRepository.findAllByIdIn(ids);
    }

    public List<EventShortDto> getEvents(String ip,
                                         String uri,
                                         String text,
                                         List<Long> categories,
                                         Boolean paid,
                                         LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd,
                                         Boolean onlyAvailable,
                                         SortType sort,
                                         Integer from,
                                         Integer size) {
        EndpointHitDto endpointHitDto = new EndpointHitDto(APPLICATION_NAME, uri, ip, LocalDateTime.now());
        client.postEndPointHit(endpointHitDto);
        Pageable page = Pagination.createPageTemplate(from, size);

        BooleanExpression resultCondition = QEvent.event.state.eq(EventState.PUBLISHED);

        if (text != null) {
            String pattern = "%" + text.toLowerCase() + "%";
            resultCondition = resultCondition.and(
                    QEvent.event.annotation.likeIgnoreCase(pattern)
                            .or(QEvent.event.description.likeIgnoreCase(pattern))
            );
        }
        if (categories != null) {
            resultCondition = resultCondition.and(
                    QEvent.event.category.id.in(categories)
            );
        }
        if (paid != null) {
            resultCondition = resultCondition.and(
                    QEvent.event.paid.eq(paid)
            );
        }
        if (rangeStart != null) {
            resultCondition.and(
                    QEvent.event.eventDate.after(rangeStart)
            );
        }
        if (rangeEnd != null) {
            resultCondition.and(
                    QEvent.event.eventDate.before(rangeEnd)
            );
        }
        if (rangeStart != null && rangeEnd != null) {
            if (rangeStart.isAfter(rangeEnd))
                throw new ValidationException("rangeStart must be earlier than rangeEnd.");
        }
        if (onlyAvailable) {
            resultCondition = resultCondition.and(
                    QEvent.event.participantLimit.eq(0)
                            .or(QEvent.event.confirmedRequests.lt(QEvent.event.participantLimit)
                            )
            );
        }

        List<Event> events = eventRepository.findAll(resultCondition, page).toList();
        Comparator<EventShortDto> comparator;
        if (sort == SortType.VIEWS) {
            comparator = (event1, event2) -> (int) (event1.getViews() - event2.getViews());
        } else {
            comparator = Comparator.comparing(EventShortDto::getEventDate);
        }
        Map<String, Long> uriHits = getUriHits(events);

        return events
                .stream()
                .map(event -> {
                    Long hits = null;
                    if (uriHits.containsKey("/events/" + event.getId()))
                        hits = uriHits.get("/events/" + event.getId());
                    return EventMapper.toEventShortDto(event, hits == null ? 0 : hits);
                })
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    public EventFullDto getEvent(String ip, String uri, Long eventId) {
        Event event = eventRepository.findByIdAndState(eventId, EventState.PUBLISHED)
                .orElseThrow(() -> new ObjectNotFoundException("Event with id=" + eventId + " doesn't exist or haven't been published yet."));
        client.postEndPointHit(new EndpointHitDto(APPLICATION_NAME, uri, ip, LocalDateTime.now()));
        return EventMapper.toEventFullDto(event, getEventViews(eventId));
    }

    public List<EventFullDto> getEventsByAdmin(
            List<Long> users,
            List<EventState> states,
            List<Long> categories,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Integer from,
            Integer size
    ) {
        Pageable page = Pagination.createPageTemplate(from, size);

        BooleanExpression resultCondition = QEvent.event.id.gt(0);

        if (users != null) {
            resultCondition = QEvent.event.initiator.id.in(users);
        }
        if (states != null) {
            resultCondition = resultCondition.and(
                    QEvent.event.state.in(states));
        }
        if (categories != null) {
            resultCondition = resultCondition.and(
                    QEvent.event.category.id.in(categories)
            );
        }
        if (rangeStart != null) {
            resultCondition.and(
                    QEvent.event.eventDate.after(rangeStart));
        }
        if (rangeEnd != null) {
            resultCondition.and(
                    QEvent.event.eventDate.before(rangeEnd)
            );
        }

        List<Event> events = eventRepository.findAll(resultCondition, page).toList();
        Map<String, Long> uriHits = getUriHits(events);

        return events
                .stream()
                .map(event -> {
                    Long hits = null;
                    if (uriHits.containsKey("/events/" + event.getId()))
                        hits = uriHits.get("/events/" + event.getId());
                    return EventMapper.toEventFullDto(event, hits == null ? 0 : hits);
                })
                .collect(Collectors.toList());

    }

    public List<EventShortDto> getUserEvents(Long userId, Integer from, Integer size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("User with id=" + userId + " doesn't exist"));
        Pageable page = Pagination.createPageTemplate(from, size);
        List<Event> events = eventRepository.findAllByInitiatorId(userId, page).toList();
        Map<String, Long> uriHits = getUriHits(events);

        return events
                .stream()
                .map(event -> {
                    Long hits = null;
                    if (uriHits.containsKey("/events/" + event.getId()))
                        hits = uriHits.get("/events/" + event.getId());
                    return EventMapper.toEventShortDto(event, hits == null ? 0 : hits);
                })
                .collect(Collectors.toList());
    }

    public EventFullDto getUserEvent(Long userId, Long eventId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("User with id=" + userId + " doesn't exist"));
        Event event = eventRepository.findByIdAndState(eventId, EventState.PENDING)
                .orElseThrow(() -> new ObjectNotFoundException("Event with id=" + eventId + " doesn't exist or haven't been published yet."));
        if (!event.getInitiator().getId().equals(userId))
            throw new UserIsNotEventInitiatorException("User with id=" + userId + " is not an initiator of event with id=" + eventId);
        return EventMapper.toEventFullDto(event, getEventViews(eventId));
    }

    public List<ParticipationRequestDto> getEventRequests(Long userId, Long eventId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("User with id=" + userId + " doesn't exist"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ObjectNotFoundException("Event with id=" + eventId + " doesn't exist."));
        if (!event.getInitiator().getId().equals(userId))
            throw new UserIsNotEventInitiatorException("User with id=" + userId + " is not an initiator of event with id=" + eventId);

        return participationRequestRepository.findAllByEventId(eventId)
                .stream()
                .map(ParticipationRequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public EventFullDto postEvent(Long initiatorId, NewEventDto newEventDto) {
        User initiator = userRepository.findById(initiatorId)
                .orElseThrow(() -> new ObjectNotFoundException("User with id=" + initiatorId + " doesn't exist"));
        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new ObjectNotFoundException("Category with id=" + newEventDto.getCategory() + " doesn't exist"));
        return EventMapper.toEventFullDto(eventRepository.save(EventMapper.toEvent(newEventDto, category, initiator)), 0L);
    }

    @Transactional
    public EventFullDto patchEvent(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event previousEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new ObjectNotFoundException("Event with id=" + eventId + " doesn't exist"));

        if (previousEvent.getState() == EventState.PUBLISHED)
            throw new EventAlreadyPublishedException("Cannot change already published event.");

        if (updateEventAdminRequest.getAnnotation() != null)
            previousEvent.setAnnotation(updateEventAdminRequest.getAnnotation());
        if (updateEventAdminRequest.getCategory() != null) {
            Category category = categoryRepository.findById(updateEventAdminRequest.getCategory())
                    .orElseThrow(() -> new ObjectNotFoundException("Category with id=" + updateEventAdminRequest.getCategory() + " doesn't exist"));
            previousEvent.setCategory(category);
        }
        if (updateEventAdminRequest.getEventDate() != null) {
            if (updateEventAdminRequest.getEventDate().isBefore(LocalDateTime.now()))
                throw new ValidationException("Cannot change event date to occurred.");
            if (!(Math.abs(Duration.between(updateEventAdminRequest.getEventDate(), LocalDateTime.now()).toMinutes()) > 60))
                throw new EventDateOccurWithinAnHour("Cannot publish event because it will occur within an hour.");
            previousEvent.setEventDate(updateEventAdminRequest.getEventDate());
        }
        if (updateEventAdminRequest.getDescription() != null)
            previousEvent.setDescription(updateEventAdminRequest.getDescription());
        if (updateEventAdminRequest.getLocation() != null) {
            Location location = LocationMapper.toLocation(updateEventAdminRequest.getLocation());
            previousEvent.setLocation(location);
        }
        if (updateEventAdminRequest.getPaid() != null)
            previousEvent.setPaid(updateEventAdminRequest.getPaid());
        if (updateEventAdminRequest.getParticipantLimit() != null)
            previousEvent.setParticipantLimit(updateEventAdminRequest.getParticipantLimit());
        if (updateEventAdminRequest.getRequestModeration() != null)
            previousEvent.setRequestModeration(updateEventAdminRequest.getRequestModeration());
        if (updateEventAdminRequest.getStateAction() != null) {
            if (previousEvent.getState() == EventState.PENDING) {
                if (updateEventAdminRequest.getStateAction() == AdminStateAction.PUBLISH_EVENT) {
                    previousEvent.setPublishedOn(LocalDateTime.now());
                    previousEvent.setState(EventState.PUBLISHED);
                } else
                    previousEvent.setState(EventState.CANCELED);
            } else
                throw new NotPendingException("Cannot change event state because it's not in the right state: " + previousEvent.getState());
        }
        if (updateEventAdminRequest.getTitle() != null)
            previousEvent.setTitle(updateEventAdminRequest.getTitle());

        return EventMapper.toEventFullDto(eventRepository.save(previousEvent), getEventViews(previousEvent.getId()));
    }

    @Transactional
    public EventFullDto patchEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("User with id=" + userId + " doesn't exist."));
        System.out.println(eventId);
        Event previousEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new ObjectNotFoundException("Event with id=" + eventId + " doesn't exist."));

        if (previousEvent.getState() == EventState.PUBLISHED)
            throw new EventAlreadyPublishedException("Cannot update event because it's already published.");

        if (updateEventUserRequest.getAnnotation() != null)
            previousEvent.setAnnotation(updateEventUserRequest.getAnnotation());
        if (updateEventUserRequest.getCategory() != null) {
            Category category = categoryRepository.findById(updateEventUserRequest.getCategory())
                    .orElseThrow(() -> new ObjectNotFoundException("Category with id=" + updateEventUserRequest.getCategory() + " doesn't exist."));
            previousEvent.setCategory(category);
        }
        if (updateEventUserRequest.getEventDate() != null) {
            if (!(updateEventUserRequest.getEventDate().isAfter(LocalDateTime.now()) && Duration.between(LocalDateTime.now(), updateEventUserRequest.getEventDate()).toMinutes() >= 120))
                throw new EventDateOccurWithinTwoHours("Cannot change event date because new date will occur within two hours.");
            previousEvent.setEventDate(updateEventUserRequest.getEventDate());
        }
        if (updateEventUserRequest.getLocation() != null) {
            Location location = LocationMapper.toLocation(updateEventUserRequest.getLocation());
            previousEvent.setLocation(location);
        }
        if (updateEventUserRequest.getPaid() != null)
            previousEvent.setPaid(updateEventUserRequest.getPaid());
        if (updateEventUserRequest.getParticipantLimit() != null)
            previousEvent.setParticipantLimit(updateEventUserRequest.getParticipantLimit());
        if (updateEventUserRequest.getRequestModeration() != null)
            previousEvent.setRequestModeration(updateEventUserRequest.getRequestModeration());
        if (updateEventUserRequest.getStateAction() != null) {
            if (updateEventUserRequest.getStateAction() == UserStateAction.SEND_TO_REVIEW) {
                previousEvent.setState(EventState.PENDING);
            } else
                previousEvent.setState(EventState.CANCELED);
        }
        if (updateEventUserRequest.getTitle() != null)
            previousEvent.setTitle(previousEvent.getTitle());

        return EventMapper.toEventFullDto(eventRepository.save(previousEvent), getEventViews(eventId));
    }

    @Transactional
    public EventRequestStatusUpdateResult patchRequests(Long userId, Long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("User with id=" + userId + " doesn't exist."));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ObjectNotFoundException("Event with id=" + eventId + " doesn't exist."));

        List<ParticipationRequest> requests = participationRequestRepository.findAllByIdIn(eventRequestStatusUpdateRequest.getRequestIds());

        if (requests.size() != eventRequestStatusUpdateRequest.getRequestIds().size())
            throw new ObjectNotFoundException("Not all participation requests were found.");

        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            List<ParticipationRequestDto> confirmedRequests = participationRequestRepository.findAllByEventIdAndStatus(eventId, ParticipationRequestStatus.CONFIRMED)
                    .stream()
                    .map(ParticipationRequestMapper::toParticipationRequestDto)
                    .collect(Collectors.toList());
            List<ParticipationRequestDto> rejectedRequests = participationRequestRepository.findAllByEventIdAndStatus(eventId, ParticipationRequestStatus.REJECTED)
                    .stream()
                    .map(ParticipationRequestMapper::toParticipationRequestDto)
                    .collect(Collectors.toList());
            return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
        }

        if (event.getConfirmedRequests().equals(event.getParticipantLimit()))
            throw new ParticipantLimitException("Cannot answer on participation requests to event with participation limit reached.");

        if (eventRequestStatusUpdateRequest.getStatus() == ParticipationRequestStatus.CONFIRMED && event.getConfirmedRequests() + eventRequestStatusUpdateRequest.getRequestIds().size() > event.getParticipantLimit())
            throw new ParticipantLimitException("The participant limit has been reached.");

        for (ParticipationRequest request : requests) {
            if (request.getStatus() != ParticipationRequestStatus.PENDING)
                throw new NotPendingException("Cannot change participation request whitch status: " + request.getStatus());
        }

        requests.forEach(request -> {
            if (eventRequestStatusUpdateRequest.getStatus() == ParticipationRequestStatus.REJECTED && request.getStatus() == ParticipationRequestStatus.CONFIRMED)
                throw new ParticipationRequestAlreadyConfirmedException("Cannot reject already confirmed participation request with id=" + request.getId());
            request.setStatus(eventRequestStatusUpdateRequest.getStatus());
        });
        if (eventRequestStatusUpdateRequest.getStatus() == ParticipationRequestStatus.CONFIRMED)
            event.setConfirmedRequests(event.getConfirmedRequests() + requests.size());

        if (event.getConfirmedRequests().equals(event.getParticipantLimit())) {
            List<ParticipationRequest> otherPendingRequest = participationRequestRepository.findAllByEventIdAndStatus(eventId, ParticipationRequestStatus.PENDING);
            otherPendingRequest.forEach(request -> request.setStatus(ParticipationRequestStatus.REJECTED));
            participationRequestRepository.saveAll(otherPendingRequest);
        }

        eventRepository.save(event);

        List<ParticipationRequestDto> confirmedRequests = participationRequestRepository.findAllByEventIdAndStatus(eventId, ParticipationRequestStatus.CONFIRMED)
                .stream()
                .map(ParticipationRequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
        List<ParticipationRequestDto> rejectedRequests = participationRequestRepository.findAllByEventIdAndStatus(eventId, ParticipationRequestStatus.REJECTED)
                .stream()
                .map(ParticipationRequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());

        return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
    }

    private Map<String, Long> getUriHits(List<Event> events) {

        return client.getViewStats(
                LocalDateTime.of(2000, 1, 1, 1, 1, 1),
                LocalDateTime.of(2100, 1, 1, 1, 1, 1),
                events
                        .stream()
                        .map(event -> "/events/" + event.getId()).toArray(String[]::new),
                true
        ).stream().collect(Collectors.toMap(ViewStatsDto::getUri, ViewStatsDto::getHits));
    }

    private Long getEventViews(Long eventId) {
        List<ViewStatsDto> viewStatsDtos = client.getViewStats(
                LocalDateTime.of(2000, 1, 1, 0, 0, 0),
                LocalDateTime.of(2100, 1, 1, 0, 0, 0),
                new String[]{
                        "/events/" + eventId
                },
                true
        );
        if (viewStatsDtos.size() == 1)
            return viewStatsDtos.get(0).getHits();
        else
            return 0L;
    }

}
