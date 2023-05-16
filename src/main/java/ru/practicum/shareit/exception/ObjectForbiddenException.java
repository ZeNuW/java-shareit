package ru.practicum.shareit.exception;

public class ObjectForbiddenException extends RuntimeException {
    public ObjectForbiddenException(String message) {
        super(message);
    }
}