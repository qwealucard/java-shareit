package ru.practicum.shareit.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void createUser() throws Exception {
        UserDto userRequestDto = new UserDto();
        userRequestDto.setName("testName");
        userRequestDto.setEmail("test@example.com");
        UserDto userDto1 = new UserDto(
                1L,
                "testName",
                "test@example.com"
        );
        when(userService.addUser(any(UserDto.class)))
                .thenReturn(userDto1);

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content(objectMapper.writeValueAsString(userRequestDto)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.name").value("testName"))
               .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void updateUser() throws Exception {
        UserDto userRequestDto = new UserDto();
        userRequestDto.setName("testName");
        userRequestDto.setEmail("test@example.com");

        UserDto userDto1 = new UserDto(
                1L,
                "testName",
                "test@example.com"
        );

        when(userService.updateUser(any(UserDto.class)))
                                                                 .thenReturn(userDto1);

        mockMvc.perform(MockMvcRequestBuilders.patch("/users/1")
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content(objectMapper.writeValueAsString(userRequestDto)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.name").value("testName"))
               .andExpect(jsonPath("$.email").value("test@example.com"))
               .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getUserById() throws Exception {
        UserDto userDto1 = new UserDto(
                1L,
                "testName",
                "test@example.com"
        );

        when(userService.findUserById(eq(1L)))
                                               .thenReturn(userDto1);

        mockMvc.perform(MockMvcRequestBuilders.get("/users/1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(1))
               .andExpect(jsonPath("$.name").value("testName"))
               .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void getAllUsers() throws Exception {
        when(userService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/users")
                                              .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(Collections.emptyList())));
    }

    @Test
    void deleteUserById() throws Exception {
        doNothing().when(userService).deleteUserById(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/users/1"))
               .andExpect(status().isOk());

        verify(userService).deleteUserById(1L);
    }
}
