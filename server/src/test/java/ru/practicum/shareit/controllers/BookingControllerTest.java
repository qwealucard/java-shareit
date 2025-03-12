package ru.practicum.shareit.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    private final String userIdHeader = "X-Sharer-User-Id";

    @Test
    void addBooking() throws Exception {

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));

        BookingDtoOut bookingDtoOut = new BookingDtoOut();
        bookingDtoOut.setId(1L);
        bookingDtoOut.setStart(LocalDateTime.now().plusDays(1));
        bookingDtoOut.setEnd(LocalDateTime.now().plusDays(2));

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        bookingDtoOut.setItem(itemDto);

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        bookingDtoOut.setBooker(userDto);

        bookingDtoOut.setStatus(Status.WAITING);

        when(bookingService.addBooking(anyLong(), any(BookingDto.class))).thenReturn(bookingDtoOut);

        mockMvc.perform(post("/bookings")
                       .header(userIdHeader, 1L)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(bookingDto)))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.id").value(1))
               .andExpect(jsonPath("$.item.id").value(1))
               .andExpect(jsonPath("$.booker.id").value(1))
               .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void approvedBooking() throws Exception {

        BookingDtoOut bookingDtoOut = new BookingDtoOut();
        bookingDtoOut.setId(1L);
        bookingDtoOut.setStart(LocalDateTime.now().plusDays(1));
        bookingDtoOut.setEnd(LocalDateTime.now().plusDays(2));

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        bookingDtoOut.setItem(itemDto);

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        bookingDtoOut.setBooker(userDto);

        bookingDtoOut.setStatus(Status.APPROVED);

        when(bookingService.approveBooking(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingDtoOut);

        mockMvc.perform(patch("/bookings/1")
                       .header(userIdHeader, 1L)
                       .param("approved", "true"))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.id").value(1))
               .andExpect(jsonPath("$.item.id").value(1))
               .andExpect(jsonPath("$.booker.id").value(1))
               .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void getBookingById() throws Exception {

        BookingDtoOut bookingDtoOut = new BookingDtoOut();
        bookingDtoOut.setId(1L);
        bookingDtoOut.setStart(LocalDateTime.now().plusDays(1));
        bookingDtoOut.setEnd(LocalDateTime.now().plusDays(2));

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        bookingDtoOut.setItem(itemDto);

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        bookingDtoOut.setBooker(userDto);

        bookingDtoOut.setStatus(Status.WAITING);

        when(bookingService.getBookingById(anyLong(), anyLong())).thenReturn(bookingDtoOut);

        mockMvc.perform(get("/bookings/1")
                       .header(userIdHeader, 1L))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.id").value(1))
               .andExpect(jsonPath("$.item.id").value(1))
               .andExpect(jsonPath("$.booker.id").value(1))
               .andExpect(jsonPath("$.status").value("WAITING"));
    }


    @Test
    void getBookings() throws Exception {

        BookingDtoOut bookingDtoOut = new BookingDtoOut();
        bookingDtoOut.setId(1L);
        bookingDtoOut.setStart(LocalDateTime.now().plusDays(1));
        bookingDtoOut.setEnd(LocalDateTime.now().plusDays(2));

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        bookingDtoOut.setItem(itemDto);

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        bookingDtoOut.setBooker(userDto);

        bookingDtoOut.setStatus(Status.WAITING);

        List<BookingDtoOut> bookingDtoOutList = Collections.singletonList(bookingDtoOut);

        when(bookingService.getBookings(anyLong(), anyString())).thenReturn(bookingDtoOutList);

        mockMvc.perform(get("/bookings")
                       .header(userIdHeader, 1L)
                       .param("status", "ALL"))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$[0].id").value(1))
               .andExpect(jsonPath("$[0].item.id").value(1))
               .andExpect(jsonPath("$[0].booker.id").value(1))
               .andExpect(jsonPath("$[0].status").value("WAITING"));
    }

    @Test
    void getOwnerBookings() throws Exception {

        BookingDtoOut bookingDtoOut = new BookingDtoOut();
        bookingDtoOut.setId(1L);
        bookingDtoOut.setStart(LocalDateTime.now().plusDays(1));
        bookingDtoOut.setEnd(LocalDateTime.now().plusDays(2));

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        bookingDtoOut.setItem(itemDto);

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        bookingDtoOut.setBooker(userDto);

        bookingDtoOut.setStatus(Status.WAITING);

        List<BookingDtoOut> bookingDtoOutList = Collections.singletonList(bookingDtoOut);

        when(bookingService.getOwnerBookings(anyLong(), anyString())).thenReturn(bookingDtoOutList);

        mockMvc.perform(get("/bookings/owner")
                       .header(userIdHeader, 1L)
                       .param("status", "ALL"))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$[0].id").value(1))
               .andExpect(jsonPath("$[0].item.id").value(1))
               .andExpect(jsonPath("$[0].booker.id").value(1))
               .andExpect(jsonPath("$[0].status").value("WAITING"));
    }
}
