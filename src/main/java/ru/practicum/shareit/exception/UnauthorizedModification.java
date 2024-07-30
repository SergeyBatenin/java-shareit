package ru.practicum.shareit.exception;

public class UnauthorizedModification extends RuntimeException {
    public UnauthorizedModification(String message) {
        super(message);
    }
}
