package ru.practicum.shareit.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.user.User;

import java.time.LocalDate;

@Data
public class ItemRequest {
    @NotNull
    private Long id;
    private String description;
    private User requestor;
    private LocalDate created;
}
