package ru.practicum.shareit.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidateException;
import ru.practicum.shareit.user.*;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserServiceTest {
    @Autowired
    UserService userService;

    static UserDto user1 = new UserDto();
    static UserDto user2 = new UserDto();

    @BeforeAll
    static void beforeAll() {
        user1.setName("name1");
        user1.setEmail("test1@example.com");
        user2.setName("name2");
        user2.setEmail("test2@example.com");
    }

    @Test
    void getAllUsersTest() {
        userService.addUser(user1);
        UserDto newUser = userService.addUser(user2);
        List<UserDto> users = userService.findAll().stream().toList();

        assertThat(users.get(1).getId()).isEqualTo(newUser.getId());
        assertThat(users.get(1).getName()).isEqualTo(newUser.getName());
        assertThat(users.get(1).getEmail()).isEqualTo(newUser.getEmail());
    }

    @Test
    void getUserByIdTest() {
        UserDto user = userService.addUser(user1);
        UserDto getUser = userService.findUserById(user.getId());

        assertThat(user.getId()).isEqualTo(getUser.getId());
        assertThat(user.getName()).isEqualTo(getUser.getName());
        assertThat(user.getEmail()).isEqualTo(getUser.getEmail());
    }

    @Test
    void duplicateEmailValidateExceptionTest() {
        UserDto user = userService.addUser(user1);

        UserDto userWithDuplicateEmail = new UserDto(
                null,
                "name3",
                "test1@example.com"
        );

        assertThatThrownBy(() -> userService.addUser(userWithDuplicateEmail))
                .isInstanceOf(ValidateException.class);
    }

    @Test
    void updateUserTest() {

        UserDto addedUser = userService.addUser(user1);
        UserDto updatedUserDto = new UserDto(addedUser.getId(), "newName", "newEmail@example.com"); // Используем ID добавленного пользователя

        UserDto result = userService.updateUser(updatedUserDto);

        assertNotNull(result);
        assertEquals(updatedUserDto.getId(), result.getId());
        assertEquals("newName", result.getName());
        assertEquals("newEmail@example.com", result.getEmail());
    }

    @Test
    void updateUserNotFoundExceptionTest() {

        UserDto updatedUserDto = new UserDto(999L, "newName", "newEmail@example.com");

        assertThatThrownBy(() -> userService.updateUser(updatedUserDto))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void updateUserDuplicateEmailValidateExceptionTest() {

        UserDto addedUser1 = userService.addUser(user1);
        UserDto addedUser2 = userService.addUser(user2);

        UserDto updatedUserDto = new UserDto(addedUser2.getId(), "newName", "test1@example.com");

        assertThatThrownBy(() -> userService.updateUser(updatedUserDto))
                .isInstanceOf(ValidateException.class);
    }

    @Test
    void updateUserCorrectNameTest() {

        UserDto addedUser = userService.addUser(user1);
        UserDto updatedUserDto = new UserDto(addedUser.getId(), "newName", null);

        UserDto result = userService.updateUser(updatedUserDto);

        assertNotNull(result);
        assertEquals(addedUser.getId(), result.getId());
        assertEquals("newName", result.getName());
        assertEquals("test1@example.com", result.getEmail());
    }

    @Test
    void updateUserCorrectEmail() {

        UserDto addedUser = userService.addUser(user1);
        UserDto updatedUserDto = new UserDto(addedUser.getId(), null, "newEmail@example.com");

        UserDto result = userService.updateUser(updatedUserDto);

        assertNotNull(result);
        assertEquals(addedUser.getId(), result.getId());
        assertEquals("name1", result.getName());
        assertEquals("newEmail@example.com", result.getEmail());
    }

    @Test
    void deleteUserByIdTest() {

        UserDto user = new UserDto(null, "deleteName", "delete@example.com");
        UserDto addedUser = userService.addUser(user);
        Long userId = addedUser.getId();

        assertDoesNotThrow(() -> userService.deleteUserById(userId));
        assertThatThrownBy(() -> userService.findUserById(userId))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void deleteUserByIdNotFoundExceptionTest() {

        Long userId = 999L;

        assertThatThrownBy(() -> userService.deleteUserById(userId))
                .isInstanceOf(NotFoundException.class);
    }
}
