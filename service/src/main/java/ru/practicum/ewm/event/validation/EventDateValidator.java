package ru.practicum.ewm.event.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.Duration;
import java.time.LocalDateTime;

public class EventDateValidator implements ConstraintValidator<EventDate, LocalDateTime> {

    @Override
    public boolean isValid(LocalDateTime eventDate, ConstraintValidatorContext context) {

        return eventDate == null || (eventDate.isAfter(LocalDateTime.now()) && Duration.between(LocalDateTime.now(), eventDate).toMinutes() >= 120);
    }

}
