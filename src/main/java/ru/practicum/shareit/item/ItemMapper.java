package ru.practicum.shareit.item;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.CommentOutDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithCommentsDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;


@UtilityClass
public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                item.getOwner().getId(),
                item.getRequest(),
                null,
                null
        );
    }

    public static Item toItem(ItemDto item) {
        return new Item(
                null,
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                null,
                item.getRequest()
        );
    }

    public static ItemWithCommentsDto toItemWithCommentsDto(ItemDto itemDto, List<CommentOutDto> comments) {
        return new ItemWithCommentsDto(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                itemDto.getOwner(),
                itemDto.getRequest(),
                itemDto.getLastBooking(),
                itemDto.getNextBooking(),
                comments
        );
    }
}
