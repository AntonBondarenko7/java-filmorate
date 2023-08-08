package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.ExistenceException;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.MpaRatingStorage;

import java.util.Collection;

@Component
@RestController
@RequiredArgsConstructor
@RequestMapping("/mpa")
public class MpaRatingController extends AdviceController {
    private final MpaRatingStorage mpa;

    @GetMapping
    public Collection<MpaRating> getAllMpaRatings() {
        return mpa.getAllMpaRatings();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMpaRatingById(@PathVariable int id) throws ExistenceException {
        return new ResponseEntity<>(mpa.getMpaRatingById(id), HttpStatus.OK);
    }
}
