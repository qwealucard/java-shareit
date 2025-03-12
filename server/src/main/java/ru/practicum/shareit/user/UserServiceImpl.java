package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidateException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class UserServiceImpl implements UserService {
    UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto addUser(UserDto userDto) {
        validateEmailUniqueness(userDto.getEmail());
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public Collection<UserDto> findAll() {
        List<User> users = userRepository.findAll();
        List<UserDto> userDtos = users.stream()
                                      .map(UserMapper::toUserDto)
                                      .toList();
        log.info("Получение всех пользователей");
        return userDtos;
    }

    @Override
    public UserDto findUserById(Long id) {
        log.info("Поиск пользователя с id: {}", id);
        return UserMapper.toUserDto(userRepository.findById(id)
                                                  .orElseThrow(() -> {
                                                      log.error("Пользователь с id {} не найден", id);
                                                      return new NotFoundException("Пользователь с id " + id + " не найден");
                                                  }));
    }

    @Override
    public UserDto updateUser(UserDto updatedUser) {
        Long userId = updatedUser.getId();
        UserDto existingUserDto = findUserById(userId);
        User existingUser = UserMapper.toUser(existingUserDto);
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
        return UserMapper.toUserDto(updated);
    }

    @Override
    public void deleteUserById(Long id) {
        UserDto userDto = findUserById(id);
        User user = UserMapper.toUser(userDto);
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
