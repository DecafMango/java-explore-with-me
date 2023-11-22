package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.EndpointHitDto;
import ru.practicum.ewm.ViewStatsDto;
import ru.practicum.ewm.dao.EndpointHitRepository;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.mapper.EndPointHitMapper;
import ru.practicum.ewm.mapper.ViewStatsMapper;
import ru.practicum.ewm.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final EndpointHitRepository repository;

    public List<ViewStatsDto> getViewStats(LocalDateTime start, LocalDateTime end, String[] uris, Boolean unique) {

        if (start != null && end != null && end.isBefore(start))
            throw new ValidationException("Parameter start mustn't be later than parameter end.");

        List<ViewStats> viewStats;

        if (uris.length != 0) {
            if (unique)
                viewStats = repository.getCertainUniqueUrisWithHits(start, end, uris);
            else
                viewStats = repository.getCertainUrisWithHits(start, end, uris);
        } else {
            if (unique)
                viewStats = repository.getUniqueUrisWithHits(start, end);
            else
                viewStats = repository.getUrisWithHits(start, end);
        }

        return viewStats.stream()
                .map(ViewStatsMapper::toViewStatsDto)
                .collect(Collectors.toList());
    }

    public EndpointHitDto postEndPointHit(EndpointHitDto endpointHitDto) {
        return EndPointHitMapper.toEndPointHitDto(repository.save(EndPointHitMapper.toEndPointHit(endpointHitDto)));
    }

}
