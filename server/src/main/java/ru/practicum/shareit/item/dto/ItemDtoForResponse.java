package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.user.dto.UserDto;

@Data
@AllArgsConstructor
public class ItemDtoForResponse {
    private Long id;
    private String name;
    private String description;
    private UserDto owner;
}
