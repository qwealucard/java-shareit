package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public interface UserService {
    User addUser(User user);

    User findUserById(Long id);

    User updateUser(Long id, User updatedUser);

    void deleteUserById(Long id);

    Collection<User> findAll();
}
