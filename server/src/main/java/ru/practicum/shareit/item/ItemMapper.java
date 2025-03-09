package ru.practicum.shareit.item;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.CommentOutDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForResponse;
import ru.practicum.shareit.item.dto.ItemWithCommentsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserMapper;

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

    public static ItemDtoForResponse toItemDtoForResponse(Item item) {
        return new ItemDtoForResponse(
                item.getId(),
                item.getName(),
                item.getDescription(),
                UserMapper.toUserDto(item.getOwner())
        );
    }
}
