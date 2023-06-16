package ru.practicum.shareit.error;

import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.exception.ErrorResponse;
import ru.practicum.shareit.exception.ObjectNotExistException;
import ru.practicum.shareit.exception.ObjectValidationException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ErrorHandlerTest {

    @InjectMocks
    private ErrorHandler errorHandler = new ErrorHandler();

    @Test
    public void testHandleObjectValidationError() {
        ObjectValidationException ex = new ObjectValidationException("Test exception");
        ErrorResponse expectedErrorResponse = new ErrorResponse(ex.getMessage());
        ErrorResponse actualErrorResponse = errorHandler.handleObjectValidationError(ex);
        assertEquals(expectedErrorResponse, actualErrorResponse);
    }

    @Test
    public void testHandleObjectNotExistError() {
        ObjectNotExistException ex = new ObjectNotExistException("Test exception");
        ErrorResponse expectedErrorResponse = new ErrorResponse(ex.getMessage());
        ErrorResponse actualErrorResponse = errorHandler.handleObjectNotExistError(ex);
        assertEquals(expectedErrorResponse, actualErrorResponse);
    }

    @Test
    public void testHandleThrowable() {
        Throwable ex = new Throwable("Test exception");
        ErrorResponse expectedErrorResponse = new ErrorResponse(ex.getMessage());
        ErrorResponse actualErrorResponse = errorHandler.handleThrowable(ex);
        assertEquals(expectedErrorResponse, actualErrorResponse);
    }
}
