package ru.practicum.ewm.exception.exceptions;

public class EventDateOccurWithinAnHour extends RuntimeException {

    public EventDateOccurWithinAnHour(String message) {
        super(message);
    }

}
