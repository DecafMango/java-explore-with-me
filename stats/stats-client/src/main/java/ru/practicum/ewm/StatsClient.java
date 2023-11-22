package ru.practicum.ewm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class StatsClient {

    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private final RestTemplate rest;

    public StatsClient(@Value("${stats-service-url}") String statsServiceUrl, RestTemplateBuilder builder) {
        rest = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(statsServiceUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();
    }

    public List<ViewStatsDto> getViewStats(LocalDateTime start, LocalDateTime end, String[] uris, Boolean unique) {
        String path = UriComponentsBuilder.fromUriString("/stats")
                .queryParam("start", start.format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)))
                .queryParam("end", end.format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)))
                .queryParam("uris", (Object[]) uris)
                .queryParam("unique", unique)
                .build()
                .toUriString();
        HttpEntity<String> requestEntity = new HttpEntity<>(defaultHeaders());
        ResponseEntity<List<ViewStatsDto>> responseEntity = rest.exchange(
                path,
                HttpMethod.GET,
                requestEntity,
                new ParameterizedTypeReference<List<ViewStatsDto>>() {
                }
        );
        return responseEntity.getBody();
    }

    public EndpointHitDto postEndPointHit(EndpointHitDto endpointHitDto) {
        HttpEntity<EndpointHitDto> requestEntity = new HttpEntity<>(endpointHitDto, defaultHeaders());
        ResponseEntity<EndpointHitDto> responseEntity = rest.exchange(
                "/hit",
                HttpMethod.POST,
                requestEntity,
                EndpointHitDto.class
        );
        return responseEntity.getBody();
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }
}
