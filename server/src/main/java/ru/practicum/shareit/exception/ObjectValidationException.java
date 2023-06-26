package ru.practicum.shareit.exception;

public class ObjectValidationException extends RuntimeException {

    public ObjectValidationException(String message) {
        super(message);
    }
}