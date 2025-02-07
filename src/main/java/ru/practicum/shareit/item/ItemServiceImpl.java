package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.*;

@Component
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;

    @Autowired
    public ItemServiceImpl(ItemStorage itemStorage) {
        this.itemStorage = itemStorage;
    }

    @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        return itemStorage.addItem(userId, itemDto);
    }

    @Override
    public ItemDto updateItem(Long userId, ItemDto updatedItemDto) {
        return itemStorage.updateItem(userId, updatedItemDto);
    }

    @Override
    public ItemDto findItemDtoById(Long id) {
        return itemStorage.findItemDtoById(id);
    }

    @Override
    public List<ItemDto> userItems(Long id) {
        return itemStorage.userItems(id);
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        return itemStorage.searchItems(text);
    }
}
