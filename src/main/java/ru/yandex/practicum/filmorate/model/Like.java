package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Like {
    private final int id;
    private final int userId;
    private final int filmId;
}
