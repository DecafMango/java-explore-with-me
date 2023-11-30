package ru.practicum.ewm.compilation.dto;

import lombok.*;

import javax.validation.constraints.Size;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
public class UpdateCompilationRequest {
    private List<Long> events;
    private Boolean pinned;
    @Size(min = 1, max = 50, message = "Title's length must me from 1 to 50 symbols.")
    private String title;
}
