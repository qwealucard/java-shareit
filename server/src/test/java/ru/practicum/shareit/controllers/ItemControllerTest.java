package ru.practicum.shareit.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentOutDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithCommentsDto;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Autowired
    private ObjectMapper objectMapper;

    private final String userIdHeader = "X-Sharer-User-Id";

    @Test
    void addItem() throws Exception {

        ItemDto itemRequestDto = new ItemDto();
        itemRequestDto.setName("testName");
        itemRequestDto.setDescription("testDescription");
        itemRequestDto.setAvailable(true);

        ItemDto itemResponseDto = new ItemDto();
        itemResponseDto.setId(1L);
        itemResponseDto.setName("testName");
        itemResponseDto.setDescription("testDescription");
        itemResponseDto.setAvailable(true);

        when(itemService.addItem(anyLong(), any(ItemDto.class))).thenReturn(itemResponseDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/items")
                                              .header(userIdHeader, 1L)
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content(objectMapper.writeValueAsString(itemRequestDto)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.name").value("testName"))
               .andExpect(jsonPath("$.description").value("testDescription"))
               .andExpect(jsonPath("$.available").value(true))
               .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void updateItem() throws Exception {

        ItemDto itemRequestDto = new ItemDto();
        itemRequestDto.setName("updatedName");
        itemRequestDto.setDescription("updatedDescription");
        itemRequestDto.setAvailable(false);

        ItemDto itemResponseDto = new ItemDto();
        itemResponseDto.setId(1L);
        itemResponseDto.setName("updatedName");
        itemResponseDto.setDescription("updatedDescription");
        itemResponseDto.setAvailable(false);

        when(itemService.updateItem(eq(1L), any(ItemDto.class)))
                .thenReturn(itemResponseDto);

        mockMvc.perform(MockMvcRequestBuilders.patch("/items/1")
                                              .header(userIdHeader, 1L)
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content(objectMapper.writeValueAsString(itemRequestDto)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.name").value("updatedName"))
               .andExpect(jsonPath("$.description").value("updatedDescription"))
               .andExpect(jsonPath("$.available").value(false))
               .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void findItemDtoById() throws Exception {
        ItemWithCommentsDto itemDto = new ItemWithCommentsDto();
        itemDto.setId(1L);
        itemDto.setName("testName");
        itemDto.setDescription("testDescription");
        itemDto.setAvailable(true);

        when(itemService.findItemDtoWithCommentsById(1L, 1L)).thenReturn(itemDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/items/1")
                                              .header(userIdHeader, 1L)
               .contentType(MediaType.APPLICATION_JSON))

               .andExpect(status().isOk())
               .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("testName"))
               .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("testDescription"))
               .andExpect(MockMvcResultMatchers.jsonPath("$.available").value(true))
               .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1));
    }

    @Test
    void userItems() throws Exception {
        when(itemService.userItems(1L)).thenReturn((Collections.emptyList()));

        mockMvc.perform(MockMvcRequestBuilders.get("/items")
                                              .header(userIdHeader, 1L))
               .andExpect(status().isOk())
               .andExpect(MockMvcResultMatchers.content().string("[]"));
    }

    @Test
    void searchItems() throws Exception {
        when(itemService.searchItems("test")).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/items/search?text=test"))
               .andExpect(status().isOk())
               .andExpect(MockMvcResultMatchers.content().string("[]"));
    }

    @Test
    void addComment() throws Exception {
        CommentOutDto commentDto = new CommentOutDto();
        commentDto.setText("testText");

        when(itemService.addComment(anyLong(), anyLong(), any(CommentDto.class)))
                .thenReturn(commentDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/items/1/comment")
                                              .header(userIdHeader, 1L)
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content(objectMapper.writeValueAsString(commentDto)))
               .andExpect(status().isOk())
               .andExpect(MockMvcResultMatchers.jsonPath("$.text").value("testText"));
    }

}
