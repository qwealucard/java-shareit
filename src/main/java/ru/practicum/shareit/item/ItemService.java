package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;
import java.util.Optional;

@Service
public interface ItemService {

    Optional<ItemDto> addItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long userId, ItemDto updatedItemDto);

    ItemDto findItemDtoById(Long id);

    List<ItemDto> userItems(Long id);

    List<ItemDto> searchItems(String text);
}
