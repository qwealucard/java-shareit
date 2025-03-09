package ru.practicum.shareit.request.dto;

import io.micrometer.core.ipc.http.HttpSender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForResponse;
import ru.practicum.shareit.item.dto.ItemWithCommentsDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ItemRequestDtoOut {
    private Long id;
    private String description;
    private UserDto user;
    private LocalDateTime created;
    private List<ItemDto> items;
}
