package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.h2.engine.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.dto.UserRequestDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserClient userClient;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void createUser() throws Exception {
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setName("testName");
        userRequestDto.setEmail("test@example.com");
        when(userClient.createUser(any(UserRequestDto.class)))
                .thenReturn(ResponseEntity.ok(userRequestDto));

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                                              .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("testName"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void updateUser() throws Exception {
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setName("testName");
        userRequestDto.setEmail("test@example.com");
        when(userClient.updateUser(anyLong(), any(UserRequestDto.class)))
                .thenReturn(ResponseEntity.ok(userRequestDto));
        mockMvc.perform(MockMvcRequestBuilders.put("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("testName"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void getUserById() throws Exception {
        UserRequestDto userDto = new UserRequestDto();
        userDto.setId(1L);
        userDto.setName("Test User");
        userDto.setEmail("test@example.com");

        when(userClient.getUserById(1L)).thenReturn(ResponseEntity.ok(userDto));

        mockMvc.perform(MockMvcRequestBuilders.get("/users/1"))
               .andExpect(status().isOk())
               .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
               .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Test User"))
               .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void getAllUsers() throws Exception {
        when(userClient.getAllUsers()).thenReturn(ResponseEntity.ok("[]"));

        mockMvc.perform(MockMvcRequestBuilders.get("/users"))
               .andExpect(status().isOk())
               .andExpect(MockMvcResultMatchers.content().string("[]"));
    }

    @Test
    void deleteUserById() throws Exception {
        when(userClient.deleteUser(1L)).thenReturn(ResponseEntity.ok(""));

        mockMvc.perform(MockMvcRequestBuilders.delete("/users/1"))
               .andExpect(status().isOk());
    }
}
