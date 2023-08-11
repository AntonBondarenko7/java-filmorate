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

import javax.validation.ConstraintViolationException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
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
        String stackTrace = getStackTraceAsString(t);

        log.debug("Непредвиденная ошибка: 500 Internal Server Error {}", stackTrace);

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("Непредвиденная ошибка", t.getMessage());
        responseBody.put("Stack Trace", stackTrace);

        return new ResponseEntity<>(responseBody, HttpStatus.INTERNAL_SERVER_ERROR);
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

    @ExceptionHandler
    public ResponseEntity<String> handleValidationConstrainException(    ConstraintViolationException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    private String getStackTraceAsString(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }
}
