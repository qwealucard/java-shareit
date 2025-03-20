package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserRequestDto {
    private Long id;
    @NotNull
    private String name;
    @Email
    @NotNull
    private String email;
}
