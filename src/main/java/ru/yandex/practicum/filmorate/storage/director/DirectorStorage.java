package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.exception.ExistenceException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorStorage {
    Director addDirector(Director director) throws ValidationException;

    List<Director> getDirectors();

    Director getDirectorById(int id) throws ValidationException, ExistenceException;

    Director updateDirector(Director director);

    void deleteDirectorById(int id);

}