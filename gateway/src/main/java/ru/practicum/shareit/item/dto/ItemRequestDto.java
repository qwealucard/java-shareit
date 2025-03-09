package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

@Data
@AllArgsConstructor
public class ItemRequestDto {
    private Long id;

    @NotBlank(message = "Название вещи не заполнено")
    private String name;

    @NotBlank(message = "Описание не заполнено")
    private String description;

    @NonNull
    private Boolean available;

    private Long owner;

    private Long requestId;
    private BookingRequestDto lastBooking;
    private BookingRequestDto nextBooking;
}
