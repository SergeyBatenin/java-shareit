package ru.practicum.shareit.exception;

public class UnauthorizedModificationItem extends RuntimeException {
    public UnauthorizedModificationItem(String message) {
        super(message);
    }
}
