package ru.pobopo.gymanager.services.user.service.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/health")
@RestController
@Slf4j
public class HealthCheckController {
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public String health() {
        return "I am alive :)";
    }
}
