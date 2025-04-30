package ru.flamexander.spring.security.jwt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.flamexander.spring.security.jwt.dtos.ServiceDto;
import ru.flamexander.spring.security.jwt.entities.Services;
import ru.flamexander.spring.security.jwt.repositories.ServiceRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceService {
    private final ServiceRepository serviceRepository;

    @Autowired
    public ServiceService(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    public Page<ServiceDto> findAll(Pageable pageable) {
        Page<Services> servicesPage = serviceRepository.findAll(pageable);
        return servicesPage.map(this::convertToDto);
    }

    public List<ServiceDto> findAll() {
        return serviceRepository.findAll().stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public ServiceDto findById(Long id) {
        Services service = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found"));
        return convertToDto(service);
    }

    public ServiceDto save(ServiceDto serviceDto) {
        Services service = new Services();
        service.setServiceName(serviceDto.getServiceName());
        service.setServicePrice(serviceDto.getServicePrice());
        service.setImageUrl(serviceDto.getImageUrl()); // Сохраняем URL картинки
        service = serviceRepository.save(service);
        return convertToDto(service);
    }

    public ServiceDto updateService(ServiceDto serviceDto) {
        Services service = serviceRepository.findById(serviceDto.getServiceId())
                .orElseThrow(() -> new RuntimeException("Service not found"));
        service.setServiceName(serviceDto.getServiceName());
        service.setServicePrice(serviceDto.getServicePrice());
        service.setImageUrl(serviceDto.getImageUrl()); // Обновляем URL картинки
        service = serviceRepository.save(service);
        return convertToDto(service);
    }

    public void deleteById(Long id) {
        serviceRepository.deleteById(id);
    }

    public List<ServiceDto> searchByName(String name) {
        return serviceRepository.findByServiceNameContainingIgnoreCase(name)
                .stream().map(this::convertToDto).collect(Collectors.toList());
    }

    private ServiceDto convertToDto(Services service) {
        ServiceDto dto = new ServiceDto();
        dto.setServiceId(service.getServiceId());
        dto.setServiceName(service.getServiceName());
        dto.setServicePrice(service.getServicePrice());
        dto.setImageUrl(service.getImageUrl()); // Добавляем URL картинки в DTO
        return dto;
    }
}
