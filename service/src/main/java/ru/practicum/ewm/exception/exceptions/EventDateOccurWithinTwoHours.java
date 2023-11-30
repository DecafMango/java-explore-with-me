package ru.practicum.ewm.exception.exceptions;

public class EventDateOccurWithinTwoHours extends RuntimeException {

    public EventDateOccurWithinTwoHours(String message) {
        super(message);
    }

}
