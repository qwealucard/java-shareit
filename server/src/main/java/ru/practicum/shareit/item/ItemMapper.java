package ru.practicum.shareit.item;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.CommentOutDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithCommentsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;

import java.util.List;


@UtilityClass
public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        Long requestId = null;
        if (item.getRequest() != null) {
            requestId = item.getRequest().getId();
        }
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                item.getOwner().getId(),
                requestId,
                null,
                null
        );
    }

    public static Item toItem(ItemDto item, ItemRequest itemRequest) {
        return new Item(
                null,
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                null,
                itemRequest
        );
    }

    public static ItemWithCommentsDto itemWithCommentsDto(ItemDto itemDto) {
        return new ItemWithCommentsDto(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                itemDto.getOwner(),
                itemDto.getRequestId(),
                itemDto.getLastBooking(),
                itemDto.getNextBooking(),
                null
        );
    }

    public static ItemWithCommentsDto toItemWithCommentsDto(ItemDto itemDto, List<CommentOutDto> comments) {
        return new ItemWithCommentsDto(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                itemDto.getOwner(),
                itemDto.getRequestId(),
                itemDto.getLastBooking(),
                itemDto.getNextBooking(),
                comments
        );
    }
}
