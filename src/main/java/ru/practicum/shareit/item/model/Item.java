package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.user.User;

@Data
@AllArgsConstructor
public class Item {
    @NotBlank
    private Long id;
    @NotBlank
    private String name;
    private String description;
    private Boolean available;
    private User owner;
    private String request;
}
