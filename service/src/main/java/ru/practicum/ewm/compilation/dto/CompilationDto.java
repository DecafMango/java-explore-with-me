package ru.practicum.ewm.compilation.dto;

import lombok.*;
import ru.practicum.ewm.event.dto.EventShortDto;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
@Getter
public class CompilationDto {
    private List<EventShortDto> events;
    private Long id;
    private Boolean pinned;
    private String title;
}
