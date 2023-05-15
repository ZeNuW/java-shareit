package ru.practicum.shareit.exception;

public class ObjectForbiddenError extends RuntimeException {
    public ObjectForbiddenError(String message) {
        super(message);
    }
}
