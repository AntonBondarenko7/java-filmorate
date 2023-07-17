package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@Builder
public class MpaRating {
    private final int id;
    private final String name;

    @JsonCreator
    public MpaRating(@JsonProperty("id") int id,
                     @JsonProperty("name") String name) {
        this.id = id;
        this.name = name;
    }
}
