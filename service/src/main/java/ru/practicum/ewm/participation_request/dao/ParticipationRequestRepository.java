package ru.practicum.ewm.participation_request.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.participation_request.model.ParticipationRequest;
import ru.practicum.ewm.participation_request.model.ParticipationRequestStatus;

import java.util.List;
import java.util.Optional;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {

    List<ParticipationRequest> findAllByRequesterId(Long userId);

    Optional<ParticipationRequest> findByRequesterIdAndEventId(Long userId, Long eventId);

    List<ParticipationRequest> findAllByEventId(Long eventId);

    List<ParticipationRequest> findAllByEventIdAndStatus(Long eventId, ParticipationRequestStatus status);

    List<ParticipationRequest> findAllByIdIn(List<Long> ids);

    @Modifying
    @Query("update ParticipationRequest p " +
            "set p.status = :newStatus " +
            "where p.event.id = :eventId " +
            "and p.status = :previousStatus")
    void updateParticipationRequestsStatus(Long eventId, ParticipationRequestStatus newStatus, ParticipationRequestStatus previousStatus);
}
