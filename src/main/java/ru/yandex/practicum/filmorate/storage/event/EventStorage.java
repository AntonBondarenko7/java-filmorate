package ru.yandex.practicum.filmorate.storage.event;

import ru.yandex.practicum.filmorate.model.event.Event;

import java.util.List;

public interface EventStorage {
    Event addEvent(Event event);
    List<Event> getUserEvent(int id);
}
