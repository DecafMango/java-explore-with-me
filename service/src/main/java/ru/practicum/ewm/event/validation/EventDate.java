package ru.practicum.ewm.event.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EventDateValidator.class)
@Documented
public @interface EventDate {
    String message() default "Event date must be in the future and not earlier than 2 hours since current moment";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
