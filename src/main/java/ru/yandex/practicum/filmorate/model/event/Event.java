package ru.yandex.practicum.filmorate.model.event;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Data
@Builder
@AllArgsConstructor
public class Event {
    private int eventId;
    private long timestamp;
    private int userId;
    @Enumerated(EnumType.STRING)
    private EventType eventType;
    @Enumerated(EnumType.STRING)
    private EventOperation operation;
    private int entityId;
}
