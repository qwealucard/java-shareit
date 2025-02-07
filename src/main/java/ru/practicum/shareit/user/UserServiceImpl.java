package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidateException;

import java.util.Collection;
import java.util.Objects;

@Component
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    UserStorage userStorage;

    private UserMapper mapper;

    @Override
    public User addUser(User user) {
        user.setId(getNextId());
        for (User anotherUser : userStorage.getUsers().values()) {
            if (Objects.equals(anotherUser.getEmail(), user.getEmail())) {
                throw new ValidateException("Пользователь с данным Email уже существует");
            }
        }
        userStorage.getUsers().put(user.getId(), user);
        log.info("Пользователь добавлен");
        return user;
    }

    public Collection<User> findAll() {
        return userStorage.getUsers().values();
    }

    public User findUserById(Long id) {
        if (!userStorage.getUsers().containsKey(id)) {
            throw new NotFoundException("Пользователь не найден");
        }
        return userStorage.getUsers().get(id);
    }

    public User updateUser(Long id, User updatedUser) {
        if (userStorage.getUsers().containsKey(id)) {
            User existingUser = userStorage.getUsers().get(id);
            if (updatedUser.getName() != null) {
                existingUser.setName(updatedUser.getName());
            }
            if (updatedUser.getEmail() != null) {
                for (User anotherUser : userStorage.getUsers().values()) {
                    if (updatedUser.getEmail().equals(anotherUser.getEmail())) {
                        throw new ValidateException("Пользователь с таким Email уже существует");
                    }
                }
                existingUser.setEmail(updatedUser.getEmail());
            }
            userStorage.getUsers().put(id, existingUser);
            return existingUser;
        } else {
            throw new NotFoundException("Пользователь не найден");
        }
    }

    public void deleteUserById(Long id) {
        if (!userStorage.getUsers().containsKey(id)) {
            throw new NotFoundException("Пользователь не найден");
        }
        userStorage.getUsers().remove(id);
        log.info("Пользователь удален");
    }

    private Long getNextId() {
        Long currentMaxId = userStorage.getUsers().keySet()
                                       .stream()
                                       .mapToLong(id -> id)
                                       .peek(id -> log.info("id сгенерирован {}", id))
                                       .max()
                                       .orElse(0L) + 1;
        return currentMaxId;
    }
}
