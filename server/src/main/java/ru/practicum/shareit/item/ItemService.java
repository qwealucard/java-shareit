package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentOutDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithCommentsDto;

import java.util.List;

@Service
public interface ItemService {

    ItemDto addItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long userId, ItemDto updatedItemDto);

    ItemDto findItemDtoById(Long id);

    List<ItemWithCommentsDto> userItems(Long id);

    List<ItemDto> searchItems(String text);

    CommentOutDto addComment(Long userId, Long itemId, CommentDto commentDto);

    ItemWithCommentsDto findItemDtoWithCommentsById(Long id);
}
