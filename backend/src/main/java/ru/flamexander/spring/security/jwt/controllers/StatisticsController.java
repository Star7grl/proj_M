package ru.flamexander.spring.security.jwt.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.flamexander.spring.security.jwt.dtos.StatItemDto;
import ru.flamexander.spring.security.jwt.service.StatisticsService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/statistics") // Эндпоинты будут доступны по /api/admin/statistics/*
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") // Защищаем контроллер, доступ только для ADMIN
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/rooms")
    public ResponseEntity<List<StatItemDto>> getRoomPopularity() {
        return ResponseEntity.ok(statisticsService.getRoomPopularityStats());
    }

    @GetMapping("/services")
    public ResponseEntity<List<StatItemDto>> getServicePopularity() {
        return ResponseEntity.ok(statisticsService.getServicePopularityStats());
    }
}
