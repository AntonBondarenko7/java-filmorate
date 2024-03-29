package ru.yandex.practicum.filmorate.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;


@Data
@Builder
public class Film {

    private int id;
    private String name;
    private String description;
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;
    private int duration;
    private final Set<Genre> genres = new LinkedHashSet<>();
    private final Set<Review> reviews = new LinkedHashSet<>();
    private Set<Director> directors;
    private MpaRating mpa;

    public void addDirector(Director director) {
        directors.add(director);
    }

    public void addReview(Review review) {
        reviews.add(review);
    }

    public void addGenre(Genre genre) {
        genres.add(genre);
    }
}
