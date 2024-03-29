package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MpaRating {
    private final int id;
    private String name;
    private String description;
}
