package ru.practicum.ewm.exception.exceptions;

public class EventAlreadyPublishedException extends RuntimeException {

    public EventAlreadyPublishedException(String message) {
        super(message);
    }

}
