package ru.practicum.shareit.item;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidateException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class ItemStorage {

    private final UserService userService;

    private ItemStorage(UserService userService) {
        this.userService = userService;
    }

    @Getter
    private final Map<Long, Item> items = new HashMap<>();

    public ItemDto addItem(Long userId, ItemDto itemDto) {
        User owner;
        try {
            owner = userService.findUserById(userId);
        } catch (NotFoundException e) {
            log.warn("Владелец с id {} не найден при добавлении вещи", userId, e);
            throw new NotFoundException("Владелец не найден");
        }

        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            log.warn("Попытка добавить вещь с пустым названием");
            throw new ValidateException("Название вещи не заполнено");
        }

        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            log.warn("Попытка добавить вещь с пустым описанием");
            throw new ValidateException("Описание не заполнено");
        }

        if (itemDto.getAvailable() == null) {
            log.warn("Попытка добавить вещь без указания статуса available");
            throw new ValidateException("Статус аренды не указан");
        }

        Item item = ItemMapper.toItem(itemDto);
        item.setId(getNextId());
        item.setOwner(owner);
        items.put(item.getId(), item);
        log.info("Вещь с id {} создана", item.getId());
        return ItemMapper.toItemDto(item);
    }


    public ItemDto updateItem(Long userId, ItemDto updatedItemDto) {
        Item existingItem;
        try {
            existingItem = findItemById(updatedItemDto.getId());
        } catch (NotFoundException e) {
            log.warn("Вещь с ID {} не найдена при обновлении", updatedItemDto.getId(), e);
            throw new NotFoundException("Вещь не найдена");
        }

        try {
            User owner = userService.findUserById(userId);
            if (!existingItem.getOwner().getId().equals(userId)) {
                log.warn("Пользователь с ID {} пытается обновить чужую вещь с ID {}", userId, updatedItemDto.getId());
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
            log.info("Вещь с ID {} обновлена", updatedItemDto.getId());
            return ItemMapper.toItemDto(existingItem);

        } catch (NotFoundException e) {
            log.warn("Пользователь с ID {} не найден при обновлении вещи с ID {}", userId, updatedItemDto.getId(), e);
            throw new NotFoundException("Пользователь не найден");
        }
    }

    private Item findItemById(Long id) {
        if (!items.containsKey(id)) {
            throw new NotFoundException("Вещи с id " + id + " не найдено");
        }
        return items.get(id);
    }

    public ItemDto findItemDtoById(Long id) {
        if (!items.containsKey(id)) {
            throw new NotFoundException("Вещи с id " + id + " не найдено");
        }
        Item item = items.get(id);
        return ItemMapper.toItemDto(item);
    }

    public List<ItemDto> userItems(Long id) {
        List<ItemDto> userItems = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getOwner() == userService.findUserById(id)) {
                userItems.add(ItemMapper.toItemDto(item));
            }
        }
        return userItems;
    }

    private List<Item> searchAvailableItems(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        return items.values().stream()
                          .filter(item -> (item.getName().toLowerCase().contains(text.toLowerCase()) ||
                                  item.getDescription().toLowerCase().contains(text.toLowerCase())) &&
                                  item.getAvailable())
                          .collect(Collectors.toList());
    }

    public List<ItemDto> searchItems(String text) {
        return searchAvailableItems(text).stream()
                                         .map(ItemMapper::toItemDto)
                                         .collect(Collectors.toList());
    }

    private Long getNextId() {
        Long currentMaxId = items.keySet()
                                       .stream()
                                       .mapToLong(id -> id)
                                       .peek(id -> log.trace("id сгенерирован {}", id))
                                       .max()
                                       .orElse(0L) + 1;
        return currentMaxId;
    }
}
