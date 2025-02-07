package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

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
        return userStorage.addUser(user);
    }

    @Override
    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    @Override
    public User findUserById(Long id) {
        return userStorage.findUserById(id);
    }

    @Override
    public User updateUser(User updatedUser) {
        return userStorage.updateUser(updatedUser);
    }

    @Override
    public void deleteUserById(Long id) {
        userStorage.deleteUserById(id);
    }
}
