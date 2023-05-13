package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public List<String> handleUserValidationError(final UserValidationException e) {
        return List.of(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public List<String> handleItemValidationError(final ItemValidationException e) {
        return List.of(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public List<String> handleUserNotExistError(final UserNotExistException e) {
        return List.of(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public List<String> handleUserAlreadyExistError(final UserAlreadyExistException e) {
        return List.of(e.getMessage());
    }

//    @ExceptionHandler
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public List<String> handleThrowable(final Throwable e) {
//        return List.of(e.getMessage());
//    }
}