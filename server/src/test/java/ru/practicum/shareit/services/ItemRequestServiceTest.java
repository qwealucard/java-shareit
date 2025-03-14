package ru.practicum.shareit.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class ItemRequestServiceTest {

    private final ItemRequestService itemRequestService;
    private final ItemService itemService;
    private final UserService userService;

    private static UserDto userDto;
    private static UserDto userDto2;
    private static ItemDto itemDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("TestName");
        userDto.setEmail("test@example.com");

        userDto2 = new UserDto();
        userDto2.setId(2L);
        userDto2.setName("TestName");
        userDto2.setEmail("test1@example.com");

        itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("TestName");
        itemDto.setAvailable(true);
    }

    @Test
    void addItemRequestTest() {
        UserDto userDto1 = userService.addUser(userDto);
        User user = UserMapper.toUser(userDto1);
        ItemRequestDto itemRequestDto = new ItemRequestDto(
                "text"
        );
        ItemRequestDtoOut itemRequestDtoOut = itemRequestService.addRequest(itemRequestDto, userDto1.getId());
        assertEquals(itemRequestDtoOut.getDescription(), "text");
        assertEquals(itemRequestDtoOut.getId(), itemRequestService.findRequestById(itemRequestDtoOut.getId()).getId());
    }

    @Test
    void addItemRequestNotFoundExceptionTest() {
        ItemRequestDto itemRequestDto = new ItemRequestDto(
                "text"
        );
        assertThatThrownBy(() -> itemRequestService.addRequest(itemRequestDto, 999L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void findAllRequestsTest() {
        UserDto userDto1 = userService.addUser(userDto);
        UserDto userDto3 = userService.addUser(userDto2);

        ItemRequestDto itemRequestDto = new ItemRequestDto(
                "text"
        );
        ItemRequestDtoOut itemRequestDtoOut = itemRequestService.addRequest(itemRequestDto, userDto1.getId());
        List<ItemRequestDtoOut> itemRequestDtoList = itemRequestService.findAllRequests(userDto3.getId());
        assertEquals(1, itemRequestDtoList.size());
    }

    @Test
    void getRequestTest() {
        UserDto userDto1 = userService.addUser(userDto);

        ItemRequestDto itemRequestDto = new ItemRequestDto(
                "text"
        );
        itemRequestService.addRequest(itemRequestDto, userDto1.getId());
        assertEquals(1L, itemRequestService.getRequests(userDto1.getId()).size());
    }

    @Test
    void getRequestNotFoundExceptionTestTest() {
        UserDto userDto1 = userService.addUser(userDto);

        ItemRequestDto itemRequestDto = new ItemRequestDto(
                "text"
        );
        ItemRequestDtoOut itemRequestDtoOut = itemRequestService.addRequest(itemRequestDto, userDto1.getId());
        assertThatThrownBy(() -> itemRequestService.findRequestById(999L))
                .isInstanceOf(NotFoundException.class);
    }
}
