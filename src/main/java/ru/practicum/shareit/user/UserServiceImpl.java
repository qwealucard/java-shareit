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

    UserStorage userStorage;

    @Autowired
    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public User addUser(User user) {
        validateEmailUniqueness(user.getEmail());
        user.setId(getNextId());
        User addedUser = userStorage.addUser(user);
        log.info("Добавлен пользователь с id: {}", user.getId());
        return addedUser;
    }

    @Override
    public Collection<User> findAll() {
        log.info("Получение всех пользователей");
        return userStorage.findAll();
    }

    @Override
    public User findUserById(Long id) {
        User user = userStorage.findUserById(id);
        if (user == null) {
            log.warn("Пользователь с id {} не найден", id);
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        log.info("Получен пользователь с id: {}", id);
        return user;
    }

    @Override
    public User updateUser(User updatedUser) {
        Long userId = updatedUser.getId();

        User existingUser = findUserById(userId);
        if (updatedUser.getName() != null) {
            existingUser.setName(updatedUser.getName());
            log.info("Имя пользователя с ID {} обновлено на {}", userId, updatedUser.getName());
        }
        if (updatedUser.getEmail() != null) {
            validateEmailUniqueness(updatedUser.getEmail(), userId);
            existingUser.setEmail(updatedUser.getEmail());
            log.info("Email пользователя с ID {} обновлен на {}", userId, updatedUser.getEmail());
        }

        User updated = userStorage.updateUser(existingUser);
        log.info("Пользователь с ID {} успешно обновлен", userId);
        return updated;
    }

    @Override
    public void deleteUserById(Long id) {
        findUserById(id);
        userStorage.deleteUserById(id);
        log.info("Удален пользователь с id: {}", id);
    }

    private Long getNextId() {
        Long currentMaxId = userStorage.findAll().stream()
                                       .mapToLong(User::getId)
                                       .max()
                                       .orElse(0L);
        log.trace("Сгенерирован id: {}", currentMaxId + 1);
        return currentMaxId + 1;
    }

    private void validateEmailUniqueness(String email) {
        validateEmailUniqueness(email, null);
    }

    private void validateEmailUniqueness(String email, Long excludedId) {
        Collection<User> users = userStorage.findAll();
        for (User user : users) {
            if (!Objects.equals(user.getId(), excludedId) && Objects.equals(user.getEmail(), email)) {
                log.warn("Попытка добавить/обновить пользователя с существующим email: {}", email);
                throw new ValidateException("Пользователь с email " + email + " уже существует");
            }
        }
    }
}
