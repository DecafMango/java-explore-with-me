package ru.practicum.ewm.exception;

import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PSQLException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentConversionNotSupportedException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.ewm.exception.exceptions.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    @ExceptionHandler(value = {
            MethodArgumentTypeMismatchException.class,
            MethodArgumentConversionNotSupportedException.class,
            MethodArgumentNotValidException.class,
            BindException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleInvalidRequestExceptions(final Exception e) {
        log.warn(e.getMessage());
        return new ApiError()
                .toBuilder()
                .status("BAD_REQUEST")
                .message(e.getMessage())
                .reason("Incorrectly made request.")
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationException(final ValidationException e) {
        log.warn(e.getMessage());
        return new ApiError()
                .toBuilder()
                .status("BAD_REQUEST")
                .message(e.getMessage())
                .reason("Incorrectly made request.")
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleObjectNotFoundException(final ObjectNotFoundException e) {
        log.warn(e.getMessage());
        return new ApiError()
                .toBuilder()
                .status("NOT_FOUND")
                .message(e.getMessage())
                .reason("The required object was not found.")
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handlePSQLException(final PSQLException e) {
        log.warn(e.getMessage());
        return new ApiError()
                .toBuilder()
                .status("CONFLICT")
                .message(e.getMessage())
                .reason("Integrity constraint has been violated.")
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleEventNotPendingException(final NotPendingException e) {
        log.warn(e.getMessage());
        return new ApiError()
                .toBuilder()
                .status("CONFLICT")
                .message(e.getMessage())
                .reason("For the requested operation the conditions are not met.")
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handleEventDateOccurWithinAnHourException(final EventDateOccurWithinAnHour e) {
        log.warn(e.getMessage());
        return new ApiError()
                .toBuilder()
                .status("FORBIDDEN")
                .message(e.getMessage())
                .reason("For the requested operation the conditions are not met.")
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleParticipationRequestExistException(final ParticipationRequestExistException e) {
        log.warn(e.getMessage());
        return new ApiError()
                .toBuilder()
                .status("CONFLICT")
                .message(e.getMessage())
                .reason("Integrity constraint has been violated.")
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleUserIsEventInitiatorException(final UserIsEventInitiatorException e) {
        log.warn(e.getMessage());
        return new ApiError()
                .toBuilder()
                .status("CONFLICT")
                .message(e.getMessage())
                .reason("Integrity constraint has been violated.")
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleEventNotPublishedException(final EventNotPublishedException e) {
        log.warn(e.getMessage());
        return new ApiError()
                .toBuilder()
                .status("CONFLICT")
                .message(e.getMessage())
                .reason("Integrity constraint has been violated.")
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleParticipantLimitException(final ParticipantLimitException e) {
        log.warn(e.getMessage());
        return new ApiError()
                .toBuilder()
                .status("CONFLICT")
                .message(e.getMessage())
                .reason("Integrity constraint has been violated.")
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleParticipationRequestAlreadyConfirmedException(final ParticipationRequestAlreadyConfirmedException e) {
        log.warn(e.getMessage());
        return new ApiError()
                .toBuilder()
                .status("CONFLICT")
                .message(e.getMessage())
                .reason("Integrity constraint has been violated.")
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleNotRequesterOfParticipationRequestException(final NotRequesterOfParticipationRequestException e) {
        log.warn(e.getMessage());
        return new ApiError()
                .toBuilder()
                .status("CONFLICT")
                .message(e.getMessage())
                .reason("Integrity constraint has been violated.")
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleUserIsNotEventInitiatorException(final UserIsNotEventInitiatorException e) {
        log.warn(e.getMessage());
        return new ApiError()
                .toBuilder()
                .status("CONFLICT")
                .message(e.getMessage())
                .reason("Integrity constraint has been violated.")
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleEventAlreadyPublishedException(final EventAlreadyPublishedException e) {
        log.warn(e.getMessage());
        return new ApiError()
                .toBuilder()
                .status("CONFLICT")
                .message(e.getMessage())
                .reason("For the requested operation the conditions are not met.")
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handleEventDateOccurWithinTwoHours(final EventDateOccurWithinTwoHours e) {
        log.warn(e.getMessage());
        return new ApiError()
                .toBuilder()
                .status("FORBIDDEN")
                .message(e.getMessage())
                .reason("For the requested operation the conditions are not met.")
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleUserIsNotCommentAuthorException(final UserIsNotCommentAuthorException e) {
        log.warn(e.getMessage());
        return new ApiError()
                .toBuilder()
                .status("CONFLICT")
                .message(e.getMessage())
                .reason("For the requested operation the conditions are not met.")
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleUnableToUpdateCommentException(final UnableToUpdateCommentException e) {
        log.warn(e.getMessage());
        return new ApiError()
                .toBuilder()
                .status("CONFLICT")
                .message(e.getMessage())
                .reason("For the requested operation the conditions are not met.")
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)))
                .build();
    }

}
