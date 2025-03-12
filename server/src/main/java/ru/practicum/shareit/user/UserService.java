package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

@Service
public interface UserService {
    UserDto addUser(UserDto user);

    UserDto findUserById(Long id);

    UserDto updateUser(UserDto updatedUser);

    void deleteUserById(Long id);

    Collection<UserDto> findAll();
}
