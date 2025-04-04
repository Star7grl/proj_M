package ru.flamexander.spring.security.jwt.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.flamexander.spring.security.jwt.dtos.ServiceDto;
import ru.flamexander.spring.security.jwt.service.ServiceService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceController {
    private final ServiceService serviceService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getServices(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<ServiceDto> servicesPage = serviceService.findAll(pageable);
        Map<String, Object> response = new HashMap<>();
        response.put("services", servicesPage.getContent());
        response.put("total", servicesPage.getTotalElements());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ServiceDto findById(@PathVariable Long id) {
        return serviceService.findById(id);
    }

    @PostMapping // Изменено: убрано "/add"
    public ServiceDto save(@RequestBody ServiceDto serviceDto) {
        return serviceService.save(serviceDto);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ServiceDto> updateService(@PathVariable Long id, @RequestBody ServiceDto serviceDto) {
        serviceDto.setServiceId(id);
        ServiceDto updatedService = serviceService.updateService(serviceDto);
        return ResponseEntity.ok(updatedService);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteById(@PathVariable Long id) {
        serviceService.deleteById(id);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ServiceDto>> searchServices(@RequestParam(required = false) String name) {
        if (name != null && !name.isEmpty()) {
            return ResponseEntity.ok(serviceService.searchByName(name));
        }
        return ResponseEntity.ok(serviceService.findAll());
    }
}