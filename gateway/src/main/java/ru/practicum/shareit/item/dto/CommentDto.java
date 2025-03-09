package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotNull;

public class CommentDto {

    @NotNull
    private String text;
}
