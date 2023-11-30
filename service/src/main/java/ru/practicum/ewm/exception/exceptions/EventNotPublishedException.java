package ru.practicum.ewm.exception.exceptions;

public class EventNotPublishedException extends RuntimeException {

    public EventNotPublishedException(String message) {
        super(message);
    }

}
