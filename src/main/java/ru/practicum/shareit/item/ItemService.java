package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@Service
public interface ItemService {

    ItemDto addItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long itemId, Long userId, ItemDto updatedItemDto);

    ItemDto findItemDtoById(Long id);

    List<ItemDto> userItems(Long id);

    List<ItemDto> searchItems(String text);
}
