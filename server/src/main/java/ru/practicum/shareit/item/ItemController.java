package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentOutDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithCommentsDto;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {

    private final ItemService itemService;
    private final String sharerUserId = "X-Sharer-User-Id";

    @Autowired
    ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto addItem(@RequestHeader(sharerUserId) Long userId,
                           @RequestBody ItemDto itemDto) {
        return itemService.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable Long itemId, @RequestHeader(sharerUserId) Long userId,
                              @RequestBody ItemDto updatedItemDto) {
        updatedItemDto.setId(itemId);
        return itemService.updateItem(userId, updatedItemDto);
    }

    @GetMapping("/{itemId}")
    public ItemWithCommentsDto findItemDtoById(@PathVariable Long itemId) {
        return itemService.findItemDtoWithCommentsById(itemId);
    }

    @GetMapping
    public List<ItemWithCommentsDto> userItems(@RequestHeader(sharerUserId) Long userId) {
        return itemService.userItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        return itemService.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentOutDto addComment(@PathVariable Long itemId,
                                    @RequestHeader(sharerUserId) Long userId,
                                    @RequestBody CommentDto commentDto) {
        return itemService.addComment(userId, itemId, commentDto);
    }
}
