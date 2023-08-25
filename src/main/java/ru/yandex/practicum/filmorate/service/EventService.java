package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.event.Event;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventStorage eventStorage;

    public Event addEvent(Event event) {
        return eventStorage.addEvent(event);
    }

    public Collection<Event> getUserEvent(int id) {
        return eventStorage.getUserEvent(id);
    }
}
