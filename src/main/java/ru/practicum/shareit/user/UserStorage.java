package ru.practicum.shareit.user;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidateException;

import java.util.*;

@Repository
@Slf4j
public class UserStorage {

    @Getter
    private final Map<Long, User> users = new HashMap<>();

    private UserMapper mapper;

    public User addUser(User user) {
        user.setId(getNextId());
        for (User anotherUser : users.values()) {
            if (Objects.equals(anotherUser.getEmail(), user.getEmail())) {
                throw new ValidateException("Пользователь с email " + user.getEmail() + " уже существует");
            }
        }
        users.put(user.getId(), user);
        log.info("Пользователь добавлен");
        return user;
    }

    public Collection<User> findAll() {
        return users.values();
    }

    public User findUserById(Long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        return users.get(id);
    }

    public User updateUser(User updatedUser) {

        if (users.containsKey(updatedUser.getId())) {
            User existingUser = users.get(updatedUser.getId());
            if (updatedUser.getName() != null) {
                existingUser.setName(updatedUser.getName());
            }
            if (updatedUser.getEmail() != null) {
                for (User anotherUser : users.values()) {
                    if (updatedUser.getEmail().equals(anotherUser.getEmail())) {
                        throw new ValidateException("Пользователь с email " + updatedUser.getEmail() +
                                " уже существует");
                    }
                }
                existingUser.setEmail(updatedUser.getEmail());
            }
            users.put(updatedUser.getId(), existingUser);
            return existingUser;
        } else {
            throw new NotFoundException("Пользователь с id" + updatedUser.getId() + "не найден");
        }
    }

    public void deleteUserById(Long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        users.remove(id);
        log.info("Пользователь удален");
    }

    private Long getNextId() {
        Long currentMaxId = users.keySet()
                                       .stream()
                                       .mapToLong(id -> id)
                                       .peek(id -> log.trace("id сгенерирован {}", id))
                                       .max()
                                       .orElse(0L) + 1;
        return currentMaxId;
    }

}
