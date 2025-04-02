    package ru.flamexander.spring.security.jwt.service;


    import lombok.RequiredArgsConstructor;
    import org.springframework.stereotype.Service;
    import ru.flamexander.spring.security.jwt.dtos.ServiceDto;
    import ru.flamexander.spring.security.jwt.entities.Booking;
    import ru.flamexander.spring.security.jwt.exceptions.ResourceNotFoundException;
    import ru.flamexander.spring.security.jwt.entities.Services;
    import ru.flamexander.spring.security.jwt.repositories.ServiceRepository;

    import java.util.List;
    import java.util.stream.Collectors;

    @Service
    @RequiredArgsConstructor
    public class ServiceService {
        private final ServiceRepository serviceRepository;

        public List<ServiceDto> findAll() {
            return serviceRepository.findAll().stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        }

        // ServiceService.java
        public ServiceDto findById(Long id) {
            Services service = serviceRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Услуга не найдена"));
            return convertToDto(service);
        }


        public ServiceDto save(ServiceDto serviceDto) {
            Services service = new Services(
                    serviceDto.getServiceId(),
                    serviceDto.getServiceName(),
                    serviceDto.getServicePrice()
            );
            Services savedService = serviceRepository.save(service);
            return convertToDto(savedService);
        }
        public Services updateService(Services service) {
            return serviceRepository.save(service);
        }

        public void deleteById(Long id) {
            serviceRepository.deleteById(id);
        }

        
        
        
        
        
        
        
        private ServiceDto convertToDto(Services service) {
            ServiceDto dto = new ServiceDto();
            dto.setServiceId(service.getServiceId());
            dto.setServiceName(service.getServiceName());
            dto.setServicePrice(service.getServicePrice());
            return dto;
        }

        public List<ServiceDto> searchByName(String serviceName) {
            return serviceRepository.findByServiceNameContainingIgnoreCase(serviceName)
                    .stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());                                                             
        }

    }
