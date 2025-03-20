package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOut;

import java.util.List;

public interface BookingService {
    BookingDtoOut addBooking(Long userId, BookingDto bookingDto);

    BookingDtoOut approveBooking(Long bookingId, Long userId, Boolean approved);

    BookingDtoOut getBookingById(Long bookingId, Long userId);

    List<BookingDtoOut> getBookings(Long userId, String state);

    List<BookingDtoOut> getOwnerBookings(Long userId, String state);
}
