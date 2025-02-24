package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class ItemStorage {

    private final Map<Long, Item> items = new HashMap<>();


    public Optional<Item> addItem(Item item) {
        items.put(item.getId(), item);
        log.info("Добавлена вещь с id: {}", item.getId());
        return Optional.of(item);
    }

    public Item updateItem(Item item) {
        items.put(item.getId(), item);
        log.info("Обновлена вещь с id: {}", item.getId());
        return item;
    }

    public Collection<Item> findAll() {
        return items.values();
    }

    public Collection<Long> findAllId() {
        return items.keySet();
    }

    public Optional<Item> findItemById(Long id) {
        Item item = items.get(id);
        return Optional.of(item);
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
