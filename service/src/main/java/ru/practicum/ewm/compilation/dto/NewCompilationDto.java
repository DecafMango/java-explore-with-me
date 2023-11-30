package ru.practicum.ewm.compilation.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
@Getter
public class NewCompilationDto {
    private List<Long> events = new ArrayList<>();
    private Boolean pinned = false;
    @NotBlank
    @Size(min = 1, max = 50, message = "Title's length must me from 1 to 50 symbols.")
    private String title;
}
