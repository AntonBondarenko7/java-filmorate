package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.ExistenceException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.util.Map;

@RestControllerAdvice
@Slf4j
public class AdviceController {
    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleValidationException(final ValidationException e) {
        log.debug("Ошибка валидации: 400 Bad Request {}", e.getMessage(), e);
        return new ResponseEntity<>(Map.of("Ошибка валидации", e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleExistenceException(final ExistenceException e) {
        log.debug("Не найден идентификатор: 404 Not Found {}", e.getMessage(), e);
        return new ResponseEntity<>(Map.of("Не найден идентификатор", e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleAllUnhandledExceptions(final Throwable t) {
        log.debug("Непредвиденная ошибка: 500 Internal Server Error {}", t.getMessage(), t);
        return new ResponseEntity<>(Map.of("Непредвиденная ошибка", t.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleNotFoundException(final NotFoundException e) {
        log.debug("Не найден идентификатор: 404 Not Found {}", e.getMessage(), e);
        return new ResponseEntity<>(Map.of("Не найден идентификатор", e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleNotValidArgumentException(final MethodArgumentNotValidException e) {
        log.debug("Ошибка валидации: 400 Bad Request {}", e.getMessage(), e);
        return new ResponseEntity<>(Map.of("Ошибка валидации", e.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
