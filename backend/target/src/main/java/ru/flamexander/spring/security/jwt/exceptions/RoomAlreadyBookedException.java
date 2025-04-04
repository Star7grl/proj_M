package ru.flamexander.spring.security.jwt.exceptions;

public class RoomAlreadyBookedException extends RuntimeException {
    public RoomAlreadyBookedException(String message) {
        super(message);
    }
}

