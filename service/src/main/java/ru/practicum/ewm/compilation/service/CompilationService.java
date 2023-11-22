package ru.practicum.ewm.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.Pagination;
import ru.practicum.ewm.StatsClient;
import ru.practicum.ewm.ViewStatsDto;
import ru.practicum.ewm.compilation.dao.CompilationRepository;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.exception.exceptions.ObjectNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventService eventService;

    private final StatsClient client;

    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        Pageable page = Pagination.createPageTemplate(from, size);
        List<Compilation> compilations;
        if (pinned != null)
            compilations = compilationRepository.findAllByPinned(pinned);
        else
            compilations = compilationRepository.findAll(page).toList();

        return compilations
                .stream()
                .map(compilation -> {
                    Map<String, Long> uriHits = getUriHits(compilation.getEvents());
                    List<EventShortDto> eventShortDtos = compilation.getEvents()
                            .stream()
                            .map(event -> {
                                Long hits = null;
                                if (uriHits.containsKey("/events/" + event.getId()))
                                    hits = uriHits.get("/events/" + event.getId());
                                return EventMapper.toEventShortDto(event, hits == null ? 0 : hits);
                            })
                            .collect(Collectors.toList());
                    return CompilationMapper.toCompilationDto(compilation, eventShortDtos);
                }).collect(Collectors.toList());
    }

    public CompilationDto getCompilation(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new ObjectNotFoundException("Compilation with id=" + compId + " doesn't exist."));

        Map<String, Long> uriHits = getUriHits(compilation.getEvents());
        List<EventShortDto> eventShortDtos = compilation.getEvents()
                .stream()
                .map(event -> {
                    Long hits = null;
                    if (uriHits.containsKey("/events/" + event.getId()))
                        hits = uriHits.get("/events/" + event.getId());
                    return EventMapper.toEventShortDto(event, hits == null ? 0 : hits);
                })
                .collect(Collectors.toList());

        return CompilationMapper.toCompilationDto(compilation, eventShortDtos);
    }

    @Transactional
    public CompilationDto postCompilation(NewCompilationDto newCompilationDto) {
        List<Event> events = eventService.getEventsById(newCompilationDto.getEvents());

        Compilation compilation = CompilationMapper.toCompilation(newCompilationDto, events);

        Map<String, Long> uriHits = getUriHits(events);
        List<EventShortDto> eventShortDtos = events
                .stream()
                .map(event -> {
                    Long hits = null;
                    if (uriHits.containsKey("/events/" + event.getId()))
                        hits = uriHits.get("/events/" + event.getId());
                    return EventMapper.toEventShortDto(event, hits == null ? 0 : hits);
                })
                .collect(Collectors.toList());

        return CompilationMapper.toCompilationDto(compilationRepository.save(compilation), eventShortDtos);
    }

    @Transactional
    public void deleteCompilation(Long compId) {
        compilationRepository.findById(compId)
                .orElseThrow(() -> new ObjectNotFoundException("Compilation with id=" + compId + " doesn't exist."));
        compilationRepository.deleteById(compId);
    }

    @Transactional
    public CompilationDto patchCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new ObjectNotFoundException("Compilation with id=" + compId + " doesn't exist."));

        if (updateCompilationRequest.getEvents() != null) {
            List<Event> events = eventService.getEventsById(updateCompilationRequest.getEvents());
            compilation.setEvents(events);
        }
        if (updateCompilationRequest.getPinned() != null)
            compilation.setPinned(updateCompilationRequest.getPinned());
        if (updateCompilationRequest.getTitle() != null)
            compilation.setTitle(updateCompilationRequest.getTitle());

        Map<String, Long> uriHits = getUriHits(compilation.getEvents());
        List<EventShortDto> eventShortDtos = compilation.getEvents()
                .stream()
                .map(event -> {
                    Long hits = null;
                    if (uriHits.containsKey("/events/" + event.getId()))
                        hits = uriHits.get("/events/" + event.getId());
                    return EventMapper.toEventShortDto(event, hits == null ? 0 : hits);
                })
                .collect(Collectors.toList());

        return CompilationMapper.toCompilationDto(compilationRepository.save(compilation), eventShortDtos);
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
}
