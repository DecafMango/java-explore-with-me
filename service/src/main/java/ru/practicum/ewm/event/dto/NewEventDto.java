package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.ewm.event.validation.EventDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;


@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
@Getter
public class NewEventDto {
    @NotBlank
    @Size(min = 20, max = 2000, message = "Annotation's length must be from 20 to 2000 symbols.")
    private String annotation;
    @NotNull(message = "Category's id mustn't be null.")
    private Long category;
    @NotBlank
    @Size(min = 20, max = 7000, message = "Description's length must be from 20 to 7000 symbols.")
    private String description;
    @NotNull(message = "Event's date mustn't be null.")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @EventDate
    private LocalDateTime eventDate;
    @NotNull(message = "Location mustn't be null")
    private LocationDto location;
    private Boolean paid = false;
    private Integer participantLimit = 0;
    private Boolean requestModeration = true;
    @NotBlank
    @Size(min = 3, max = 120, message = "Title's length must be from 3 to 120 symbols.")
    private String title;
}
