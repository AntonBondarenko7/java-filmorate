package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ExistenceException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.validator.DirectorValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorStorage directorStorage;

    public Director addDirector(Director director) {
        DirectorValidator.validateDirector(director);
        return directorStorage.addDirector(director);
    }

    public List<Director> getDirectors() {
        return directorStorage.getDirectors();
    }

    public Director getDirectorById(int id) throws ExistenceException, ValidationException {
        return directorStorage.getDirectorById(id);
    }

    public void updateDirector(Director director) {
        DirectorValidator.validateDirector(director);
        directorStorage.updateDirector(director);
    }

    public void deleteDirectorById(int id) {
        directorStorage.deleteDirectorById(id);
    }
}
