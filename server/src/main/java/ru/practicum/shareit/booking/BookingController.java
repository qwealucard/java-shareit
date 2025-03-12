package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOut;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingController {

    private final BookingService bookingService;
    private final String header = "X-Sharer-User-Id";

    @PostMapping
    public BookingDtoOut addBooking(@RequestHeader(header) Long userId,
                                    @RequestBody BookingDto bookingDto) {
        return bookingService.addBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoOut approvedBooking(@RequestHeader(header) Long userId,
                                         @PathVariable Long bookingId,
                                         @RequestParam Boolean approved) {
        return bookingService.approveBooking(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoOut getBookingById(@RequestHeader(header) Long userId,
                                        @PathVariable Long bookingId) {
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDtoOut> getBookings(@RequestHeader(header) Long userId,
                                           @RequestParam(defaultValue = "ALL") String status) {
        return bookingService.getBookings(userId, status);
    }

    @GetMapping("/owner")
    public List<BookingDtoOut> getOwnerBookings(@RequestHeader(header) Long userId,
                                                @RequestParam(defaultValue = "ALL") String status) {
        return bookingService.getOwnerBookings(userId, status);
    }
}
