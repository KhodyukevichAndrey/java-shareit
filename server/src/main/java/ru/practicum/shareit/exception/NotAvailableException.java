package ru.practicum.shareit.exception;

public class NotAvailableException extends RuntimeException {
    public NotAvailableException(final String message) {
        super(message);
    }
}
