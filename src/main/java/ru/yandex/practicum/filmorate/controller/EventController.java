package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.service.EventService;

@Component
@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class EventController {
    private final EventService eventService;

    @GetMapping()
    public ResponseEntity<?> getUsersFeed(@PathVariable int id) {
        return new ResponseEntity<>(eventService.getUserEvent(id), HttpStatus.OK);
    }
}
