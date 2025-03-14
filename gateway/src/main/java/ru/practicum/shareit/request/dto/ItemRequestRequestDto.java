package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ItemRequestRequestDto {
    private Long id;

    @NotNull
    private String description;
}
