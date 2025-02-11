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
    private final ItemStorage itemStorage;
    private final UserService userService;

    @Autowired
    public ItemServiceImpl(ItemStorage itemStorage, UserService userService) {
        this.itemStorage = itemStorage;
        this.userService = userService;
    }

    @Override
    public Optional<ItemDto> addItem(Long userId, ItemDto itemDto) {
        User owner;
        try {
            owner = userService.findUserById(userId);
        } catch (NotFoundException e) {
            log.warn("Владелец с ID {} не найден при добавлении вещи", userId, e);
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
        Item addedItem = itemStorage.addItem(item);
        log.info("Вещь с ID {} создана", addedItem.getId());
        return Optional.of(ItemMapper.toItemDto(addedItem));
    }

    @Override
    public ItemDto updateItem(Long userId, ItemDto updatedItemDto) {
        Item existingItem;

        if (!itemStorage.itemExists(updatedItemDto.getId())) {
            log.warn("Вещь с ID {} не найдена при обновлении", updatedItemDto.getId());
            throw new NotFoundException("Вещь не найдена");
        }
        existingItem = itemStorage.findItemById(updatedItemDto.getId());
        try {
            User owner = userService.findUserById(userId);
            if (!existingItem.getOwner().getId().equals(userId)) {
                log.warn("Пользователь с ID {} пытается обновить чужую вещь с ID {}", userId, updatedItemDto.getId());
                throw new ForbiddenException("Вы не владелец этой вещи");
            }
            if (updatedItemDto.getName() != null) {
                existingItem.setName(updatedItemDto.getName());
                log.info("Имя вещи с ID {} обновлено на {}", updatedItemDto.getId(), updatedItemDto.getName());
            }
            if (updatedItemDto.getDescription() != null) {
                existingItem.setDescription(updatedItemDto.getDescription());
                log.info("Описание вещи с ID {} обновлено", updatedItemDto.getId());
            }
            if (updatedItemDto.getAvailable() != null) {
                existingItem.setAvailable(updatedItemDto.getAvailable());
                log.info("Статус available вещи с ID {} обновлен", updatedItemDto.getId());
            }
            Item updatedItem = itemStorage.updateItem(existingItem);
            log.info("Вещь с ID {} обновлена", updatedItemDto.getId());
            return ItemMapper.toItemDto(updatedItem);
        } catch (NotFoundException e) {
            log.warn("Владелец с ID {} не найден при обновлении вещи", userId, e);
            throw new NotFoundException("Владелец не найден");
        } catch (ForbiddenException e) {
            throw e;
        }
    }

    @Override
    public ItemDto findItemDtoById(Long id) {
        Item item = itemStorage.findItemById(id);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> userItems(Long userId) {
        List<Item> items = itemStorage.findItemsByUserId(userId);
        return items.stream()
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
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
                                       .peek(id -> log.trace("id сгенерирован {}", id))
                                       .max()
                                       .orElse(0L) + 1;
        return currentMaxId;
    }
}
