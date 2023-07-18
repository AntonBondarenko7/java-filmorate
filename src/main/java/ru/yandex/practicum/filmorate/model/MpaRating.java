package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MpaRating {
    private final int id;
    private final String name;
    private final String description;

    @JsonCreator
    public MpaRating(@JsonProperty("id") int id,
                     @JsonProperty("name") String name,
                     @JsonProperty("description") String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
}
