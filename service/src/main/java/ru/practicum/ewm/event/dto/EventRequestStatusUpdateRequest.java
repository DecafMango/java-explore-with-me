package ru.practicum.ewm.event.dto;

import lombok.Getter;
import lombok.ToString;
import ru.practicum.ewm.participation_request.model.ParticipationRequestStatus;

import java.util.List;

@ToString
@Getter
public class EventRequestStatusUpdateRequest {
    List<Long> requestIds;
    ParticipationRequestStatus status;
}
