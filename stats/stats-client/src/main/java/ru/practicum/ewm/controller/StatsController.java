package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.EndpointHitDto;
import ru.practicum.ewm.client.StatsClient;

import javax.validation.Valid;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatsController {
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private final StatsClient client;

    @GetMapping("/stats")
    public ResponseEntity<Object> getViewStats(
            @RequestParam @DateTimeFormat(pattern = DATE_TIME_PATTERN) LocalDateTime start,
            @RequestParam @DateTimeFormat(pattern = DATE_TIME_PATTERN) LocalDateTime end,
            @RequestParam(required = false, defaultValue = "") String[] uris,
            @RequestParam(required = false, defaultValue = "false") Boolean unique
    ) {
        log.info("Getting stats: start={}, end={}, uris={}, unique={}", start, end, uris, unique);
        return client.getViewStats(start, end, uris, unique);
    }

    @PostMapping("/hit")
    public ResponseEntity<Object> postEndpointHit(@Valid @RequestBody EndpointHitDto endpointHitDto) {
        log.info("Posting endpoint hit {}", endpointHitDto);
        return client.postEndPointHit(endpointHitDto);
    }

}
