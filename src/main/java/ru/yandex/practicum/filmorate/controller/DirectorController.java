package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ExistenceException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/directors")
public class DirectorController {
    private final DirectorService directorService;

    @Autowired
    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    @PostMapping
    public Director addDirector(@RequestBody Director director) throws ValidationException {
        log.info("Пришел /POST запрос на добавление режиссёра: {}", director);
        return directorService.addDirector(director);
    }

    @GetMapping
    public List<Director> getDirectors() {
        log.info("Пришел /GET запрос на получение всех режиссёров");
        return directorService.getDirectors();
    }

    @GetMapping("/{id}")
    public Director getDirectorById(@PathVariable int id) throws ExistenceException, ValidationException {
        log.info("Получен /GET запрос на получение режиссёра с id = {}", id);
        return directorService.getDirectorById(id);
    }

    @PutMapping
    public Director updateDirector(@RequestBody Director director) throws ExistenceException, ValidationException {
        log.info("Получен /PUT запрос на изменение данных режиссёра с id = {}", director.getId());
        directorService.updateDirector(director);
        return directorService.getDirectorById(director.getId());
    }

    @DeleteMapping("/{id}")
    public void deleteDirectorById(@PathVariable int id) {
        log.info("Получен /DELETE запрос на удаление режиссёра с id = {}", id);
        directorService.deleteDirectorById(id);
    }
}
