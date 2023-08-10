package ru.yandex.practicum.filmorate.storage.event;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.event.Event;
import ru.yandex.practicum.filmorate.model.event.EventType;
import ru.yandex.practicum.filmorate.model.event.EventOperation;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class EventDbStorage implements EventStorage {
    private final NamedParameterJdbcOperations jdbcOperations;
    private static final Logger log = LoggerFactory.getLogger(EventStorage.class);

    @Override
    public Event addEvent(Event event) {
        String sqlQuery = "insert into EVENT (TIMESTAMP, USER_ID, EVENT_TYPE, OPERATION, ENTITY_ID) "
                + "values (:timestamp, :user_id, :event_type, :operation, :entity_id)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource mapQuery = getMapQuery(event);
        jdbcOperations.update(sqlQuery, mapQuery, keyHolder);
        event.setEventId(keyHolder.getKey().intValue());
        log.info("Event add: " + event);
        return event;
    }

    @Override
    public List<Event> getUserEvent(int id) {
        final String sqlQuery =
                "select * " +
                "from EVENT " +
                "where USER_ID = :id";
        return jdbcOperations.query(sqlQuery, Map.of("id", id), new FeedRowMapper());
    }

    private MapSqlParameterSource getMapQuery(Event event) {
        MapSqlParameterSource mapToReturn = new MapSqlParameterSource();
        mapToReturn.addValue("timestamp", event.getTimestamp());
        mapToReturn.addValue("user_id", event.getUserId());
        mapToReturn.addValue("event_type", event.getEventType().toString());
        mapToReturn.addValue("operation", event.getOperation().toString());
        mapToReturn.addValue("entity_id", event.getEntityId());
        return mapToReturn;
    }

    @Component
    private static class FeedRowMapper implements RowMapper<Event> {
        @Override
        public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Event(rs.getInt("EVENT_ID"),
                    rs.getLong("TIMESTAMP"),
                    rs.getInt("USER_ID"),
                    EventType.valueOf(rs.getString("EVENT_TYPE")),
                    EventOperation.valueOf(rs.getString("OPERATION")),
                    rs.getInt("ENTITY_ID")
            );
        }
    }
}
