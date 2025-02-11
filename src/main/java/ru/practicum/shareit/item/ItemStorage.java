package ru.practicum.shareit.item;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class ItemStorage {

    @Getter
    private final Map<Long, Item> items = new HashMap<>();


    public Item addItem(Item item) {
        items.put(item.getId(), item);
        log.info("Добавлена вещь с id: {}", item.getId());
        return item;
    }

    public Item updateItem(Item item) {
        items.put(item.getId(), item);
        log.info("Обновлена вещь с id: {}", item.getId());
        return item;
    }

    public Item findItemById(Long id) {
        Item item = items.get(id);
        if (item == null) {
            log.warn("Вещь с id {} не найдена", id);
            throw new NotFoundException("Вещи с id " + id + " не найдено");
        }
        return item;
    }

    public List<Item> findItemsByUserId(Long userId) {
        log.info("Получение вещей пользователя с id {}", userId);
        return items.values().stream()
                    .filter(item -> item.getOwner().getId().equals(userId))
                    .collect(Collectors.toList());
    }

    public boolean itemExists(Long itemId) {
        return items.containsKey(itemId);
    }
}
