package ru.practicum.ewm.participation_request.mapper;

import ru.practicum.ewm.participation_request.dto.ParticipationRequestDto;
import ru.practicum.ewm.participation_request.model.ParticipationRequest;

public class ParticipationRequestMapper {

    public static ParticipationRequestDto toParticipationRequestDto(ParticipationRequest participationRequest) {
        return new ParticipationRequestDto()
                .toBuilder()
                .id(participationRequest.getId())
                .created(participationRequest.getCreated())
                .requester(participationRequest.getRequester().getId())
                .event(participationRequest.getEvent().getId())
                .status(participationRequest.getStatus())
                .build();
    }

}
