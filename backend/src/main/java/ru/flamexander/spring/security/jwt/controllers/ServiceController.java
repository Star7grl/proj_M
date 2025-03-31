package ru.flamexander.spring.security.jwt.controllers;

import lombok.RequiredArgsConstructor;
import org.hibernate.service.Service;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.flamexander.spring.security.jwt.dtos.ServiceDto;
import ru.flamexander.spring.security.jwt.entities.Booking;
import ru.flamexander.spring.security.jwt.entities.Services;
import ru.flamexander.spring.security.jwt.exceptions.ResourceNotFoundException;
import ru.flamexander.spring.security.jwt.service.ServiceService;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceController {
    private final ServiceService serviceService;

    @GetMapping
    public List<ServiceDto> findAll() {
        return serviceService.findAll();
    }

    @GetMapping("/{id}")
    public ServiceDto findById(@PathVariable Long id) {
        return serviceService.findById(id);
    }

    //localhost:8080/api/services/add
    @PostMapping("/add")
    public ServiceDto save(@RequestBody ServiceDto serviceDto) {
        return serviceService.save(serviceDto);
    }


    @PutMapping("/update/{id}")
    public Services updateService(@PathVariable Long id, @RequestBody Services service) {
        // Возможно, здесь вам потребуется дополнительно установить id для сущности, если оно не передается в теле запроса
        service.setServiceId(id); // Предполагая, что у вас есть метод setServiceId
        return serviceService.updateService(service);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteById(@PathVariable Long id) {
        serviceService.deleteById(id);
    }


    //http://localhost:8080/api/services/search?name=Уборка
    @GetMapping("/search")
    public ResponseEntity<List<ServiceDto>> searchServices(@RequestParam(required = false) String name) {
        if (name != null && !name.isEmpty()) {
            return ResponseEntity.ok(serviceService.searchByName(name));
        }
        return ResponseEntity.ok(serviceService.findAll());
    }

}
