package ru.practicum.shareit.services;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.exceptions.CommentNotBookingItemException;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentOutDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithCommentsDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Slf4j
public class ItemServiceTest {

    private final ItemRequestService itemRequestService;
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;

    @Autowired
    private ItemServiceTest(ItemService itemService, ItemRequestService itemRequestService, UserService userService,
                            BookingService bookingService) {
        this.itemService = itemService;
        this.userService = userService;
        this.itemRequestService = itemRequestService;
        this.bookingService = bookingService;
    }

    private static UserDto user1;
    private static UserDto user2;

    private static UserDto ownerDto;
    private static ItemDto itemDto;

    @BeforeAll
    static void beforeAll() {
        user1 = new UserDto();
        user1.setName("name1");
        user1.setEmail("test1@example.com");

        user2 = new UserDto();
        user2.setName("name2");
        user2.setEmail("test2@example.com");

        ownerDto = new UserDto();
        ownerDto.setName("Owner");
        ownerDto.setEmail("owner@example.com");

        itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);
    }

    @Test
    void addItemTest() {

        UserDto owner = userService.addUser(user1);
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);

        ItemDto addedItem = itemService.addItem(owner.getId(), itemDto);

        assertThat(addedItem).isNotNull();
        assertThat(addedItem.getName()).isEqualTo("Test Item");
        assertThat(addedItem.getDescription()).isEqualTo("Test Description");
        assertThat(addedItem.getAvailable()).isTrue();
    }

    @Test
    void addItemUserNotFoundExceptionTest() {

        Long nonExistentUserId = 999L;
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);

        assertThatThrownBy(() -> itemService.addItem(nonExistentUserId, itemDto))
                .isInstanceOf(ru.practicum.shareit.exceptions.NotFoundException.class);
    }

    @Test
    void addItemWithRequestTest() {

        ItemRequestDto itemRequestDto = new ItemRequestDto("Test Description");
        UserDto owner = userService.addUser(user1);
        UserDto requester = userService.addUser(user2);
        ItemRequestDtoOut itemRequest = itemRequestService.addRequest(itemRequestDto, requester.getId());
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);
        itemDto.setRequestId(itemRequest.getId());

        ItemDto addedItem = itemService.addItem(owner.getId(), itemDto);

        assertThat(addedItem).isNotNull();
        assertThat(addedItem.getName()).isEqualTo("Test Item");
        assertThat(addedItem.getDescription()).isEqualTo("Test Description");
        assertThat(addedItem.getAvailable()).isTrue();
    }

    @Test
    void updateItemTest() {
        ownerDto = userService.addUser(ownerDto);
        ItemDto addedItemDto = itemService.addItem(ownerDto.getId(), itemDto);
        ItemDto updatedItemDto = new ItemDto();
        updatedItemDto.setId(addedItemDto.getId());
        updatedItemDto.setName("Updated Name");
        updatedItemDto.setDescription("Updated Description");
        updatedItemDto.setAvailable(false);

        ItemDto result = itemService.updateItem(ownerDto.getId(), updatedItemDto);

        assertNotNull(result);
        assertEquals(updatedItemDto.getId(), result.getId());
        assertEquals(updatedItemDto.getName(), result.getName());
        assertEquals(updatedItemDto.getDescription(), result.getDescription());
        assertEquals(updatedItemDto.getAvailable(), result.getAvailable());
    }

    @Test
    void updateItemItemNotFoundExceptionTest() {

        ItemDto updatedItemDto = new ItemDto();
        updatedItemDto.setId(999L);

        assertThatThrownBy(() -> itemService.updateItem(ownerDto.getId(), updatedItemDto))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void updateItemForbiddenExceptionTest() {

        ownerDto = userService.addUser(ownerDto);
        ItemDto addedItemDto = itemService.addItem(ownerDto.getId(), itemDto);

        UserDto anotherUserDto = new UserDto();
        anotherUserDto.setName("Another User");
        anotherUserDto.setEmail("another@example.com");
        anotherUserDto = userService.addUser(anotherUserDto);

        ItemDto updatedItemDto = new ItemDto();
        updatedItemDto.setId(addedItemDto.getId());
        updatedItemDto.setName("Updated Name");

        UserDto finalAnotherUserDto = anotherUserDto;
        assertThatThrownBy(() -> itemService.updateItem(finalAnotherUserDto.getId(), updatedItemDto))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void updateItemUserNotFoundException() {

        ownerDto = userService.addUser(ownerDto);
        ItemDto addedItemDto = itemService.addItem(ownerDto.getId(), itemDto);

        ItemDto updatedItemDto = new ItemDto();
        updatedItemDto.setId(addedItemDto.getId());
        updatedItemDto.setName("Updated Name");

        assertThatThrownBy(() -> itemService.updateItem(999L, updatedItemDto))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void updateItemOnlyNameCorrectTest() {

        ownerDto = userService.addUser(ownerDto);
        ItemDto addedItemDto = itemService.addItem(ownerDto.getId(), itemDto);
        ItemDto updatedItemDto = new ItemDto();
        updatedItemDto.setId(addedItemDto.getId());
        updatedItemDto.setName("Updated Name");

        ItemDto result = itemService.updateItem(ownerDto.getId(), updatedItemDto);

        assertNotNull(result);
        assertEquals(addedItemDto.getId(), result.getId());
        assertEquals("Updated Name", result.getName());
        assertEquals(addedItemDto.getDescription(), result.getDescription());
        assertEquals(addedItemDto.getAvailable(), result.getAvailable());
    }

    @Test
    void updateItemOnlyDescriptionCorrectTest() {

        ownerDto = userService.addUser(ownerDto);
        ItemDto addedItemDto = itemService.addItem(ownerDto.getId(), itemDto);
        ItemDto updatedItemDto = new ItemDto();
        updatedItemDto.setId(addedItemDto.getId());
        updatedItemDto.setDescription("Updated Description");

        ItemDto result = itemService.updateItem(ownerDto.getId(), updatedItemDto);

        assertNotNull(result);
        assertEquals(addedItemDto.getId(), result.getId());
        assertEquals(addedItemDto.getName(), result.getName());
        assertEquals("Updated Description", result.getDescription());
        assertEquals(addedItemDto.getAvailable(), result.getAvailable());
    }

    @Test
    void updateItemOnlyAvailableCorrectTest() {

        ownerDto = userService.addUser(ownerDto);
        ItemDto addedItemDto = itemService.addItem(ownerDto.getId(), itemDto);
        ItemDto updatedItemDto = new ItemDto();
        updatedItemDto.setId(addedItemDto.getId());
        updatedItemDto.setAvailable(false);

        ItemDto result = itemService.updateItem(ownerDto.getId(), updatedItemDto);

        assertNotNull(result);
        assertEquals(addedItemDto.getId(), result.getId());
        assertEquals(addedItemDto.getName(), result.getName());
        assertEquals(addedItemDto.getDescription(), result.getDescription());
        assertEquals(false, result.getAvailable());
    }

    @Test
    void findItemByIdTest() {
        UserDto owner = userService.addUser(user1);
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);

        ItemDto addedItem = itemService.addItem(owner.getId(), itemDto);
        ItemWithCommentsDto findItem = itemService.findItemDtoById(addedItem.getId());

        assertEquals(addedItem.getId(), findItem.getId());
        assertEquals(addedItem.getName(), findItem.getName());
        assertEquals(addedItem.getDescription(), findItem.getDescription());
        assertEquals(addedItem.getAvailable(), findItem.getAvailable());
    }

    @Test
    void findItemByIDNotFoundTest() {

        Long itemId = 999L;

        assertThrows(NotFoundException.class, () -> itemService.findItemDtoById(itemId));
    }

    @Test
    void findItemDtoWithCommentsByIdTest() {

        UserDto owner = userService.addUser(user1);
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);

        ItemDto addedItem = itemService.addItem(owner.getId(), itemDto);

        ItemWithCommentsDto result = itemService.findItemDtoWithCommentsById(addedItem.getId(), user1.getId());

        assertNotNull(result);
        assertEquals(itemDto.getName(), result.getName());
        assertEquals(itemDto.getDescription(), result.getDescription());
        assertEquals(itemDto.getAvailable(), result.getAvailable());
        assertTrue(result.getComments().isEmpty());
    }

    @Test
    void findItemDtoWithCommentsByIdNotFoundExceptionTest() {

        Long itemId = 999L;

        assertThrows(NotFoundException.class, () -> itemService.findItemDtoWithCommentsById(itemId, user1.getId()));
    }

    @Test
    void searchAvailableItemsTest() {
        UserDto userDto = userService.addUser(user1);
        ItemDto itemDto1 = itemService.addItem(userDto.getId(), itemDto);
        List<ItemDto> items = List.of(itemDto1);
        assertEquals(items.size(), itemService.searchItems("Test Description").size());
    }

    @Test
    void addCommentTest() {
        UserDto userDto = userService.addUser(user1);
        UserDto userDto1 = userService.addUser(user2);
        ItemDto itemDto1 = itemService.addItem(userDto.getId(), itemDto);
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(itemDto1.getId());
        bookingDto.setStart(LocalDateTime.now().minusDays(2L));
        bookingDto.setEnd(LocalDateTime.now().minusDays(1L));

        User user = UserMapper.toUser(userDto);
        Item item = ItemMapper.toItem(itemDto1, null);
        User user3 = UserMapper.toUser(userDto1);

        BookingDtoOut bookingOut = bookingService.addBooking(userDto1.getId(), bookingDto);
        bookingService.approveBooking(bookingOut.getId(), userDto.getId(), true);
        CommentDto commentDto = new CommentDto(1L, "text");
        Comment comment = CommentMapper.toComment(commentDto, item, user3);
        item.setOwner(user);
        itemDto1.setAvailable(true);
        CommentOutDto commentOutDto = CommentMapper.commentOutDto(comment);
        CommentMapper.toCommentDto(comment);
        CommentOutDto commentOutDto1 = itemService.addComment(userDto1.getId(), itemDto1.getId(), commentDto);
        assertEquals(commentOutDto1.getText(), commentOutDto.getText());
    }

    @Test
    void addCommentNotFoundExceptionItemTest() {
        UserDto userDto = userService.addUser(user1);
        UserDto userDto1 = userService.addUser(user2);
        ItemDto itemDto1 = itemService.addItem(userDto.getId(), itemDto);
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(itemDto1.getId());
        bookingDto.setStart(LocalDateTime.now().minusDays(2L));
        bookingDto.setEnd(LocalDateTime.now().minusDays(1L));

        User user = UserMapper.toUser(userDto);
        Item item = ItemMapper.toItem(itemDto1, null);
        User user3 = UserMapper.toUser(userDto1);

        BookingDtoOut bookingOut = bookingService.addBooking(userDto1.getId(), bookingDto);
        bookingService.approveBooking(bookingOut.getId(), userDto.getId(), true);
        CommentDto commentDto = new CommentDto(1L, "text");
        Comment comment = CommentMapper.toComment(commentDto, item, user3);
        item.setOwner(user);
        itemDto1.setAvailable(true);
        CommentOutDto commentOutDto = CommentMapper.commentOutDto(comment);
        assertThatThrownBy(() -> itemService.addComment(userDto1.getId(), 999L, commentDto))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void addCommentNotFoundExceptionUserTest() {
        UserDto userDto = userService.addUser(user1);
        ItemDto itemDto1 = itemService.addItem(userDto.getId(), itemDto);
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(itemDto1.getId());
        bookingDto.setStart(LocalDateTime.now().minusDays(2L));
        bookingDto.setEnd(LocalDateTime.now().minusDays(1L));
        User user = UserMapper.toUser(userDto);
        Item item = ItemMapper.toItem(itemDto1, null);
        CommentDto commentDto = new CommentDto(1L, "text");
        Comment comment = CommentMapper.toComment(commentDto, item, user);
        item.setOwner(user);
        itemDto1.setAvailable(true);
        assertThatThrownBy(() -> itemService.addComment(999L, itemDto1.getId(), commentDto))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void addCommentTestCommentNotBookingItemExceptionTest() {
        UserDto userDto = userService.addUser(user1);
        UserDto userDto1 = userService.addUser(user2);
        ItemDto itemDto1 = itemService.addItem(userDto.getId(), itemDto);
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(itemDto1.getId());
        bookingDto.setStart(LocalDateTime.now().minusDays(2L));
        bookingDto.setEnd(LocalDateTime.now().minusDays(1L));

        User user = UserMapper.toUser(userDto);
        Item item = ItemMapper.toItem(itemDto1, null);

        CommentDto commentDto = new CommentDto(1L, "text");
        item.setOwner(user);
        itemDto1.setAvailable(true);
        assertThatThrownBy(() -> itemService.addComment(userDto1.getId(), itemDto1.getId(), commentDto))
                .isInstanceOf(CommentNotBookingItemException.class);
    }


    @Test
    void userItems() {
        UserDto userDto = userService.addUser(user1);
        ItemDto itemDto1 = itemService.addItem(userDto.getId(), itemDto);
        ItemWithCommentsDto itemWithCommentsDto = ItemMapper.toItemWithCommentsDto(itemDto1, null);
        List<ItemWithCommentsDto> items = List.of(itemWithCommentsDto);
        assertEquals(items.size(), itemService.userItems(userDto.getId()).size());
    }
}
