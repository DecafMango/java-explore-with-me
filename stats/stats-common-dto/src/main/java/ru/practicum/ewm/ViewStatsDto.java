package ru.practicum.ewm;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
public class ViewStatsDto {
    private String app;
    private String uri;
    private Long hits;
}
