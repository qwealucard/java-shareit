package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidateException;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Component
@Slf4j
public class UserServiceImpl implements UserService {
    UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User addUser(User user) {
        validateEmailUniqueness(user.getEmail());
        Optional<User> addedUser = Optional.of(userRepository.save(user));
        return addedUser.orElseThrow(() -> {
            log.error("Не удалось добавить пользователя с id: {}", user.getId());
            return new ValidateException("Не удалось добавить пользователя");
        });
    }

    @Override
    public Collection<User> findAll() {
        log.info("Получение всех пользователей");
        return userRepository.findAll();
    }

    @Override
    public User findUserById(Long id) {
        log.info("Поиск пользователя с id: {}", id);
        return userRepository.findById(id)
                             .orElseThrow(() -> {
                                 log.error("Пользователь с id {} не найден", id);
                                 return new NotFoundException("Пользователь с id " + id + " не найден");
                             });
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

        User updated = userRepository.save(existingUser);
        log.info("Пользователь с ID {} успешно обновлен", userId);
        return updated;
    }

    @Override
    public void deleteUserById(Long id) {
        User user = findUserById(id);
        userRepository.delete(user);
        log.info("Удален пользователь с id: {}", id);
    }


    private void validateEmailUniqueness(String email) {
        validateEmailUniqueness(email, null);
    }

    private void validateEmailUniqueness(String email, Long excludedId) {
        Collection<User> users = userRepository.findAll();
        for (User user : users) {
            if (!Objects.equals(user.getId(), excludedId) && Objects.equals(user.getEmail(), email)) {
                log.error("Попытка добавить/обновить пользователя с существующим email: {}", email);
                throw new ValidateException("Пользователь с email " + email + " уже существует");
            }
        }
    }
}
