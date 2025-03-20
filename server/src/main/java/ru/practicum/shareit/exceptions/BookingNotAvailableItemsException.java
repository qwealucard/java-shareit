package ru.practicum.shareit.exceptions;

public class BookingNotAvailableItemsException extends RuntimeException {
    public BookingNotAvailableItemsException(String message) {
        super(message);
    }
}
