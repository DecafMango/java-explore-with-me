package ru.practicum.ewm.event.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;

import java.util.List;
import java.util.Optional;


public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {

    Page<Event> findAllByInitiatorId(Long userId, Pageable page);

    Optional<Event> findByIdAndState(Long eventId, EventState state);

    List<Event> findAllByIdIn(List<Long> ids);
}
