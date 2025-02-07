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

    private final ItemService itemService;
    private final String sharerUserId = "X-Sharer-User-Id";

    @Autowired
    ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto addItem(@Valid @RequestHeader(sharerUserId) Long userId,
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
    public ItemDto findItemDtoById(@PathVariable Long itemId) {
        return itemService.findItemDtoById(itemId);
    }

    @GetMapping
    public List<ItemDto> userItems(@RequestHeader(sharerUserId) Long userId) {
        return itemService.userItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        return itemService.searchItems(text);
    }
}
