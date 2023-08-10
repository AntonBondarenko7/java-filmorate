package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.ExistenceException;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.util.Map;

@RestControllerAdvice
@Slf4j
public class AdviceController {
    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleValidationException(final ValidationException e) {
        log.debug("Ошибка валидации {}", e.getMessage(), e);
        return new ResponseEntity<>(Map.of("Ошибка валидации", e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleExistenceException(final ExistenceException e) {
        log.debug("Не найден идентификатор {}", e.getMessage(), e);
        return new ResponseEntity<>(Map.of("Не найден идентификатор", e.getMessage()), HttpStatus.NOT_FOUND);
    }


}
