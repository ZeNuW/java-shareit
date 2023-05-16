package ru.practicum.shareit.exception;

public class ObjectNotExistException extends RuntimeException {
    public ObjectNotExistException(String message) {
        super(message);
    }
}