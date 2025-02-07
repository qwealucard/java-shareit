package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
public class ItemController {

    @Autowired
    ItemService itemService;

    @PostMapping
    public ItemDto addItem(@Valid @RequestHeader("X-Sharer-User-Id") Long userId,
                           @RequestBody ItemDto itemDto) {
        return itemService.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId,
                              @RequestBody ItemDto updatedItemDto) {
        return itemService.updateItem(itemId, userId, updatedItemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto findItemDtoById(@PathVariable Long itemId) {
        return itemService.findItemDtoById(itemId);
    }

    @GetMapping
    public List<ItemDto> userItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.userItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        return itemService.searchItems(text);
    }
}
