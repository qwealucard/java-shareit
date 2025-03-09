package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@Slf4j
public class UserStorage {

    private final Map<Long, User> users = new HashMap<>();

    public Optional<User> addUser(User user) {
        users.put(user.getId(), user);
        return Optional.of(user);
    }

    public Collection<User> findAll() {
        return users.values();
    }

    public Optional<User> findUserById(Long id) {
        log.info("Поиск пользователя с id {} в хранилище", id);
        return Optional.ofNullable(users.get(id));
    }

    public User updateUser(User user) {
        users.put(user.getId(), user);
        log.info("Обновление пользователя с id {} в хранилище", user.getId());
        return user;
    }

    public void deleteUserById(Long id) {
        users.remove(id);
        log.info("Удален пользователь с id: {}", id);
    }
}
