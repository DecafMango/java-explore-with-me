package ru.practicum.ewm.event.dto;

import lombok.*;
import ru.practicum.ewm.participation_request.dto.ParticipationRequestDto;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
@Getter
public class EventRequestStatusUpdateResult {
    List<ParticipationRequestDto> confirmedRequests;
    List<ParticipationRequestDto> rejectedRequests;
}
