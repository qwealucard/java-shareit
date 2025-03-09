package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingForItemDto;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ItemWithCommentsDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long owner;
    private Long request;
    private BookingForItemDto lastBooking;
    private BookingForItemDto nextBooking;
    private List<CommentOutDto> comments;
}
