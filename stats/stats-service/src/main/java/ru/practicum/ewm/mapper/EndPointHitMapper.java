package ru.practicum.ewm.mapper;

import ru.practicum.ewm.EndpointHitDto;
import ru.practicum.ewm.model.EndPointHit;

public class EndPointHitMapper {

    public static EndPointHit toEndPointHit(EndpointHitDto endpointHitDto) {
        return new EndPointHit().toBuilder()
                .app(endpointHitDto.getApp())
                .uri(endpointHitDto.getUri())
                .ip(endpointHitDto.getIp())
                .timestamp(endpointHitDto.getTimestamp())
                .build();
    }

    public static EndpointHitDto toEndPointHitDto(EndPointHit endPointHit) {
        return new EndpointHitDto().toBuilder()
                .app(endPointHit.getApp())
                .uri(endPointHit.getUri())
                .ip(endPointHit.getIp())
                .timestamp(endPointHit.getTimestamp())
                .build();
    }
}
