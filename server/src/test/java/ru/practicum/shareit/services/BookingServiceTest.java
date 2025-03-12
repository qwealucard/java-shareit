package ru.practicum.shareit.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.exceptions.BookingNotAvailableItemsException;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidateException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class BookingServiceTest {

    private final BookingService bookingService;
    private final ItemService itemService;
    private final UserService userService;

    @Autowired
    BookingServiceTest(BookingService bookingService, ItemService itemService, UserService userService) {
        this.itemService = itemService;
        this.bookingService = bookingService;
        this.userService = userService;
    }

    private UserDto booker;
    private ItemDto itemDto;
    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {

        booker = new UserDto();
        booker.setId(1L);
        booker.setName("Test Name");
        booker.setEmail("test@example.com");

        itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Test Name");

        bookingDto = new BookingDto();
        //bookingDto.setItemId(1L);
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
    }

    @Test
    void addBookingTest() {
        UserDto userDto = userService.addUser(booker);
        itemDto.setAvailable(true);
        ItemDto itemDto1 = itemService.addItem(userDto.getId(), itemDto);
        bookingDto.setItemId(itemDto1.getId());
        BookingDtoOut result = bookingService.addBooking(userDto.getId(), bookingDto);
        assertNotNull(result);
    }

    @Test
    void addBookingNotFoundExceptionUserTest() {
        UserDto userDto = userService.addUser(booker);
        itemDto.setAvailable(true);
        ItemDto itemDto1 = itemService.addItem(userDto.getId(), itemDto);
        bookingDto.setItemId(itemDto1.getId());
        Long userId = 999L;
        assertThatThrownBy(() -> bookingService.addBooking(userId, bookingDto))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void addBookingBookingNotAvailableItemsExceptionTest() {

        UserDto user = userService.addUser(booker);
        itemDto.setAvailable(false);
        ItemDto itemDto1 = itemService.addItem(user.getId(), itemDto);

        bookingDto.setItemId(itemDto1.getId());


        BookingNotAvailableItemsException exception = assertThrows(BookingNotAvailableItemsException.class,
                () -> bookingService.addBooking(user.getId(), bookingDto));
        assertEquals("Вещь недоступна для бронирования.", exception.getMessage());

    }
//    @Test
//    void addBookingStartValidateExceptionTest() {
//        UserDto userDto = userService.addUser(booker);
//        itemDto.setAvailable(true);
//        ItemDto itemDto1 = itemService.addItem(userDto.getId(), itemDto);
//        bookingDto.setItemId(itemDto1.getId());
//        bookingDto.setStart(LocalDateTime.now().minusDays(1L));
//        assertThatThrownBy(() -> bookingService.addBooking(userDto.getId(), bookingDto))
//                .isInstanceOf(ValidateException.class);
//    }

    @Test
    void addBookingEndValidateExceptionTest() {
        UserDto userDto = userService.addUser(booker);
        itemDto.setAvailable(true);
        ItemDto itemDto1 = itemService.addItem(userDto.getId(), itemDto);
        bookingDto.setItemId(itemDto1.getId());
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now().minusDays(2L));
        assertThatThrownBy(() -> bookingService.addBooking(userDto.getId(), bookingDto))
                .isInstanceOf(ValidateException.class);
    }

    @Test
    void addBookingNotFoundExceptionItemTest() {
        Long itemId = 999L;
        UserDto userDto = userService.addUser(booker);
        bookingDto.setItemId(itemId);
        assertThatThrownBy(() -> bookingService.addBooking(userDto.getId(), bookingDto))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void approveBookingTest() {
        UserDto userDto = userService.addUser(booker);
        itemDto.setAvailable(true);
        ItemDto itemDto1 = itemService.addItem(userDto.getId(), itemDto);
        bookingDto.setItemId(itemDto1.getId());
        BookingDtoOut bookingDtoOut1 = bookingService.addBooking(userDto.getId(), bookingDto);
        BookingDtoOut bookingDtoOut = bookingService.approveBooking(bookingDtoOut1.getId(), userDto.getId(), true);
        assertEquals(bookingDtoOut.getStatus(), Status.APPROVED);
    }

    @Test
    void approveBookingFalseAvailableTest() {
        UserDto userDto = userService.addUser(booker);
        itemDto.setAvailable(true);
        ItemDto itemDto1 = itemService.addItem(userDto.getId(), itemDto);
        bookingDto.setItemId(itemDto1.getId());
        BookingDtoOut bookingDtoOut1 = bookingService.addBooking(userDto.getId(), bookingDto);
        BookingDtoOut bookingDtoOut = bookingService.approveBooking(bookingDtoOut1.getId(), userDto.getId(), false);
        assertEquals(bookingDtoOut.getStatus(), Status.REJECTED);
    }

    @Test
    void approvedBookingForbiddenExceptionTest() {
        UserDto userDto = userService.addUser(booker);
        UserDto userDto1 = new UserDto(
                10L,
                "Test Name1",
                "test1@example.com"
        );
        userService.addUser(userDto1);
        itemDto.setAvailable(true);
        ItemDto itemDto1 = itemService.addItem(userDto.getId(), itemDto);
        bookingDto.setItemId(itemDto1.getId());
        BookingDtoOut bookingDtoOut1 = bookingService.addBooking(userDto.getId(), bookingDto);
        assertThatThrownBy(() -> bookingService.approveBooking(bookingDtoOut1.getId(), userDto1.getId(), true))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void getBookingByIdTest() {
        UserDto userDto = userService.addUser(booker);
        itemDto.setAvailable(true);
        ItemDto itemDto1 = itemService.addItem(userDto.getId(), itemDto);
        bookingDto.setItemId(itemDto1.getId());
        BookingDtoOut bookingDtoOut1 = bookingService.addBooking(userDto.getId(), bookingDto);
        assertEquals(bookingService.getBookingById(bookingDtoOut1.getId(), userDto.getId()), bookingDtoOut1);
    }

    @Test
    void getBookingByIdNotFoundExceptionTest() {
        UserDto userDto = userService.addUser(booker);
        itemDto.setAvailable(true);
        ItemDto itemDto1 = itemService.addItem(userDto.getId(), itemDto);
        bookingDto.setItemId(itemDto1.getId());
        BookingDtoOut bookingDtoOut1 = bookingService.addBooking(userDto.getId(), bookingDto);
        assertThatThrownBy(() -> bookingService.getBookingById(999L, userDto.getId()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getBookingsTest() {
        UserDto userDto = userService.addUser(booker);
        itemDto.setAvailable(true);
        ItemDto itemDto1 = itemService.addItem(userDto.getId(), itemDto);
        bookingDto.setItemId(itemDto1.getId());
        BookingDtoOut bookingDtoOut1 = bookingService.addBooking(userDto.getId(), bookingDto);
        Booking booking = new Booking();
        BookingMapper.toBookingForItemDto(booking);

        List<BookingDtoOut> bookings = List.of(bookingDtoOut1);
        assertEquals(bookingService.getBookings(userDto.getId(), "WAITING"), bookings);
    }

    @Test
    void getBookingsStateNullTest() {
        UserDto userDto = userService.addUser(booker);
        itemDto.setAvailable(true);
        ItemDto itemDto1 = itemService.addItem(userDto.getId(), itemDto);
        bookingDto.setItemId(itemDto1.getId());
        BookingDtoOut bookingDtoOut1 = bookingService.addBooking(userDto.getId(), bookingDto);
        List<BookingDtoOut> bookings = List.of(bookingDtoOut1);
        assertEquals(bookingService.getBookings(userDto.getId(), null), bookings);
    }

    @Test
    void getBookingsValidateExceptionTest() {
        UserDto userDto = userService.addUser(booker);
        itemDto.setAvailable(true);
        ItemDto itemDto1 = itemService.addItem(userDto.getId(), itemDto);
        bookingDto.setItemId(itemDto1.getId());
        BookingDtoOut bookingDtoOut1 = bookingService.addBooking(userDto.getId(), bookingDto);
        List<BookingDtoOut> bookings = List.of(bookingDtoOut1);
        assertThatThrownBy(() -> bookingService.getBookings(userDto.getId(), "Unknown"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getOwnerBookings() {
        UserDto userDto = userService.addUser(booker);
        itemDto.setAvailable(true);
        ItemDto itemDto1 = itemService.addItem(userDto.getId(), itemDto);
        bookingDto.setItemId(itemDto1.getId());
        BookingDtoOut bookingDtoOut1 = bookingService.addBooking(userDto.getId(), bookingDto);
        List<BookingDtoOut> bookings = List.of(bookingDtoOut1);
        assertEquals(bookingService.getOwnerBookings(userDto.getId(), "WAITING"), bookings);
    }

    @Test
    void getOwnerBookingsIllegalArgumentExceptionExceptionTest() {
        UserDto userDto = userService.addUser(booker);
        itemDto.setAvailable(true);
        ItemDto itemDto1 = itemService.addItem(userDto.getId(), itemDto);
        bookingDto.setItemId(itemDto1.getId());
        BookingDtoOut bookingDtoOut1 = bookingService.addBooking(userDto.getId(), bookingDto);
        List<BookingDtoOut> bookings = List.of(bookingDtoOut1);
        assertThatThrownBy(() -> bookingService.getBookings(userDto.getId(), "Unknown"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}

