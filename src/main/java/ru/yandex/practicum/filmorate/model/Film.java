package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;


@Data
@Builder
public class Film {

    private int id;
    private final String name;
    private final String description;
    private Set<Integer> likes;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private final LocalDate releaseDate;
    private final int duration;
    private Set<Genre> genres;
    private MpaRating mpa;

    @JsonCreator
    public Film(@JsonProperty("id") int id,
                @JsonProperty("name") String name,
                @JsonProperty("description") String description,
                @JsonProperty("likes") Set<Integer> likes,
                @JsonProperty("releaseDate") LocalDate releaseDate,
                @JsonProperty("duration") int duration,
                @JsonProperty("genres") Set<Genre> genres,
                @JsonProperty("mpa")MpaRating mpa) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.likes = likes;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.genres = genres;
        this.mpa = mpa;
    }
}
