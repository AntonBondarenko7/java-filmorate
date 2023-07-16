package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FilmGenre {
    private final int id;
    private final int filmId;
    private final int genreId;
}
