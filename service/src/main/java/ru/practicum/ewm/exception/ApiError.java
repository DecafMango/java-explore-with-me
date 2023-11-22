package ru.practicum.ewm.exception;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Getter
public class ApiError {
    private String status;
    private String message;
    private String reason;
    private String timestamp;
}
