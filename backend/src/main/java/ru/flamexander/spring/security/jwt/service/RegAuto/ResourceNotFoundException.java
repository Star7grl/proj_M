package ru.flamexander.spring.security.jwt.service.RegAuto;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
