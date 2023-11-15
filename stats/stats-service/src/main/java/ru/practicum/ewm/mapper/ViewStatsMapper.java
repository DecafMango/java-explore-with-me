package ru.practicum.ewm.mapper;

import ru.practicum.ewm.ViewStatsDto;
import ru.practicum.ewm.model.ViewStats;

public class ViewStatsMapper {

    public static ViewStatsDto toViewStatsDto(ViewStats viewStats) {
        return new ViewStatsDto().toBuilder()
                .app(viewStats.getApp())
                .uri(viewStats.getUri())
                .hits(viewStats.getHits())
                .build();
    }

}
