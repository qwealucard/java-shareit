package ru.practicum.shareit.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private ObjectMapper objectMapper;

    private final String userIdHeader = "X-Sharer-User-Id";

    @Test
    void addRequest() throws Exception {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Нужна лопата для уборки снега");

        ItemRequestDtoOut itemRequestDtoOut = new ItemRequestDtoOut();
        UserDto user = new UserDto();
        itemRequestDtoOut.setId(1L);
        itemRequestDtoOut.setDescription("Нужна лопата для уборки снега");
        itemRequestDtoOut.setUser(user);
        itemRequestDtoOut.setCreated(LocalDateTime.now());

        when(itemRequestService.addRequest(any(ItemRequestDto.class), anyLong()))
                .thenReturn(itemRequestDtoOut);

        mockMvc.perform(post("/requests")
                       .header(userIdHeader, 1L)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(itemRequestDto)))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.id").value(1));
    }


    @Test
    void getRequests() throws Exception {
        ItemRequestDtoOut itemRequestDtoOut = new ItemRequestDtoOut();
        UserDto user = new UserDto();
        itemRequestDtoOut.setId(1L);
        itemRequestDtoOut.setDescription("Нужна лопата для уборки снега");
        itemRequestDtoOut.setUser(user);
        itemRequestDtoOut.setCreated(LocalDateTime.now());

        List<ItemRequestDtoOut> itemRequestDtoOutList = Collections.singletonList(itemRequestDtoOut);

        when(itemRequestService.getRequests(anyLong())).thenReturn(itemRequestDtoOutList);

        mockMvc.perform(get("/requests")
                       .header(userIdHeader, 1L)
                       .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$[0].id").value(1))
               .andExpect(jsonPath("$[0].description").value("Нужна лопата для уборки снега"));
    }

    @Test
    void findAllRequests() throws Exception {
        ItemRequestDtoOut itemRequestDtoOut = new ItemRequestDtoOut();
        UserDto user = new UserDto();
        itemRequestDtoOut.setId(1L);
        itemRequestDtoOut.setDescription("Нужна лопата для уборки снега");
        itemRequestDtoOut.setUser(user);
        itemRequestDtoOut.setCreated(LocalDateTime.now());

        List<ItemRequestDtoOut> itemRequestDtoOutList = Collections.singletonList(itemRequestDtoOut);

        when(itemRequestService.findAllRequests(anyLong())).thenReturn(itemRequestDtoOutList);

        mockMvc.perform(get("/requests/all")
                       .header(userIdHeader, 1L)
                       .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$[0].id").value(1))
               .andExpect(jsonPath("$[0].description").value("Нужна лопата для уборки снега"));
    }

    @Test
    void findRequestById() throws Exception {
        ItemRequestDtoOut itemRequestDtoOut = new ItemRequestDtoOut();
        UserDto user = new UserDto();
        itemRequestDtoOut.setId(1L);
        itemRequestDtoOut.setDescription("Нужна лопата для уборки снега");
        itemRequestDtoOut.setUser(user);
        itemRequestDtoOut.setCreated(LocalDateTime.now());

        when(itemRequestService.findRequestById(anyLong())).thenReturn(itemRequestDtoOut);

        mockMvc.perform(get("/requests/1")
                       .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.id").value(1))
               .andExpect(jsonPath("$.description").value("Нужна лопата для уборки снега"));
    }
}
