package ru.practicum.ewm.model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
public class ViewStats {
    private String app;
    private String uri;
    private Long hits;
}
