package ru.yandex.practicum.filmorate.utils;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Comparator;

public class FilmComparator implements Comparator<Film> {

    @Override
    public int compare(Film f1, Film f2) {
        int f1LikesCount = f1.getLikes().toArray().length;
        int f2LikesCount = f2.getLikes().toArray().length;
        return Integer.compare(f1LikesCount, f2LikesCount);
    }
}
