package ru.practicum.ewm.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.model.EndPointHit;
import ru.practicum.ewm.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface EndpointHitRepository extends JpaRepository<EndPointHit, Long> {

    @Query("select new ru.practicum.ewm.model.ViewStats(e.app, e.uri, count(e)) " +
            "from EndPointHit as e " +
            "where e.timestamp between :start and :end " +
            "group by e.app, e.uri " +
            "order by count(e) desc, e.uri")
    List<ViewStats> getUrisWithHits(LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.ewm.model.ViewStats(e.app, e.uri, count(e)) " +
            "from EndPointHit as e " +
            "where e.timestamp between :start and :end " +
            "and e.uri in :uris " +
            "group by e.app, e.uri " +
            "order by count(e) desc, e.uri")
    List<ViewStats> getCertainUrisWithHits(LocalDateTime start, LocalDateTime end, String[] uris);

    @Query("select new ru.practicum.ewm.model.ViewStats(e.app, e.uri, count(distinct e.ip)) " +
            "from EndPointHit as e " +
            "where e.timestamp between :start and :end " +
            "group by e.app, e.uri " +
            "order by count(e) desc, e.uri")
    List<ViewStats> getUniqueUrisWithHits(LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.ewm.model.ViewStats(e.app, e.uri, count(distinct e.ip)) " +
            "from EndPointHit as e " +
            "where e.timestamp between :start and :end " +
            "and e.uri in :uris " +
            "group by e.app, e.uri " +
            "order by count(e) desc, e.uri")
    List<ViewStats> getCertainUniqueUrisWithHits(LocalDateTime start, LocalDateTime end, String[] uris);

}
