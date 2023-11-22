package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.ToString;
import ru.practicum.ewm.event.model.UserStateAction;
import ru.practicum.ewm.event.validation.EventDate;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@ToString
@Getter
public class UpdateEventUserRequest {
    @Size(min = 20, max = 2000, message = "Annotation's length must be from 20 to 2000 symbols.")
    private String annotation;
    private Long category;
    @Size(min = 20, max = 7000, message = "Description's length must be from 20 to 7000 symbols.")
    private String description;
    @EventDate
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private LocationDto location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private UserStateAction stateAction;
    @Size(min = 3, max = 120, message = "Title's length must be from 3 to 120 symbols.")
    private String title;
}
