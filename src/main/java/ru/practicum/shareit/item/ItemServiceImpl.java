package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidateException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ItemServiceImpl implements ItemService {

    @Autowired
    ItemStorage itemStorage;

    @Autowired
    UserService userService;

    @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        try {
            User owner = userService.findUserById(userId);
            Item item = ItemMapper.toItem(itemDto);
            if (itemDto.getName().isBlank()) {
                throw new ValidateException("Название вещи не заполнено");
            }
            if (itemDto.getDescription().isBlank()) {
                throw new ValidateException("Описание не заполнено");
            }
            if (itemDto.getAvailable() == null) {
                throw new ValidateException("Статус аренды не указан");
            }
            item.setId(getNextId());
            item.setOwner(owner);
            itemStorage.getItems().put(item.getId(), item);
            log.info("Вещь создана");
            return ItemMapper.toItemDto(item);
        } catch (NotFoundException e) {
            throw new NotFoundException("Владелец не найден");
        }
    }

    @Override
    public ItemDto updateItem(Long itemId, Long userId, ItemDto updatedItemDto) {
        try {
            User owner = userService.findUserById(userId);
            Item existingItem = findItemById(itemId);
            if (!existingItem.getOwner().equals(userService.findUserById(userId))) {
                throw new ForbiddenException("Вы не владелец этой вещи");
            }
            if (updatedItemDto.getName() != null) {
                existingItem.setName(updatedItemDto.getName());
            }
            if (updatedItemDto.getDescription() != null) {
                existingItem.setDescription(updatedItemDto.getDescription());
            }
            if (updatedItemDto.getAvailable() != null) {
                existingItem.setAvailable(updatedItemDto.getAvailable());
            }
            log.info("Вещь обновлена");
            return ItemMapper.toItemDto(existingItem);
        } catch (ValidateException e) {
            throw new ValidateException("Ошибка с заполнением полей");
        } catch (NotFoundException e) {
            throw new NotFoundException("Объект не найден");
        }
    }

    @Override
    public ItemDto findItemDtoById(Long id) {
        if (!itemStorage.getItems().containsKey(id)) {
            throw new NotFoundException("Вещи с таким id не найдено");
        }
        Item item = itemStorage.getItems().get(id);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> userItems(Long id) {
        List<ItemDto> userItems = new ArrayList<>();
        for (Item item : itemStorage.getItems().values()) {
            if (item.getOwner() == userService.findUserById(id)) {
                userItems.add(ItemMapper.toItemDto(item));
            }
        }
        return userItems;
    }

    private Item findItemById(Long id) {
        if (!itemStorage.getItems().containsKey(id)) {
            throw new NotFoundException("Вещи с таким id не найдено");
        }
        return itemStorage.getItems().get(id);
    }

    private List<Item> searchAvailableItems(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        return itemStorage.getItems().values().stream()
                          .filter(item -> (item.getName().toLowerCase().contains(text.toLowerCase()) ||
                                  item.getDescription().toLowerCase().contains(text.toLowerCase())) &&
                                  item.getAvailable())
                          .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        return searchAvailableItems(text).stream()
                                         .map(ItemMapper::toItemDto)
                                         .collect(Collectors.toList());
    }

    private Long getNextId() {
        Long currentMaxId = itemStorage.getItems().keySet()
                                       .stream()
                                       .mapToLong(id -> id)
                                       .peek(id -> log.info("id сгенерирован {}", id))
                                       .max()
                                       .orElse(0L) + 1;
        return currentMaxId;
    }
}
