package ru.practicum.shareit.exceptions;

public class CommentNotBookingItemException extends RuntimeException {
    public CommentNotBookingItemException(String message) {
        super(message);
    }
}
