package ru.practicum.ewm.event.mapper;

import ru.practicum.ewm.event.dto.LocationDto;
import ru.practicum.ewm.event.model.Location;

public class LocationMapper {

    public static Location toLocation(LocationDto locationDto) {
        return new Location()
                .toBuilder()
                .lat(locationDto.getLat())
                .lon(locationDto.getLon())
                .build();
    }

    public static LocationDto toLocationDto(Location location) {
        return new LocationDto()
                .toBuilder()
                .lat(location.getLat())
                .lon(location.getLon())
                .build();
    }

}
