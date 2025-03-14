package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingForItemDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private Long owner;

    private Long requestId;
    private BookingForItemDto lastBooking;
    private BookingForItemDto nextBooking;
}
