package ru.practicum.shareit.booking;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOut;

import java.util.List;

@Service
public interface BookingService {
    BookingDtoOut addBooking(Long userId, BookingDto bookingDto);

    BookingDtoOut approveBooking(Long bookingId, Long userId, Boolean approved);

    BookingDtoOut getBookingById(Long bookingId, Long userId);

    List<BookingDtoOut> getBookings(Long userId, String state);

    List<BookingDtoOut> getOwnerBookings(Long userId, String state);
}
