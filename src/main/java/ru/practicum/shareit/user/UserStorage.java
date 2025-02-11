package ru.practicum.shareit.user;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@Slf4j
public class UserStorage {

    @Getter
    private final Map<Long, User> users = new HashMap<>();

    public User addUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    public Collection<User> findAll() {
        return users.values();
    }

    public User findUserById(Long id) {
        log.info("Поиск пользователя с id {} в хранилище", id);
        return users.get(id);
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
