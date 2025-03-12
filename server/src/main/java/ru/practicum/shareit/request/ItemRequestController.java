package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    private final String header = "X-Sharer-User-Id";

    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ItemRequestDtoOut addRequest(@RequestHeader(header) Long userId,
                                        @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.addRequest(itemRequestDto, userId);
    }

    @GetMapping
    public List<ItemRequestDtoOut> getRequests(@RequestHeader(header) Long userId) {
        return itemRequestService.getRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoOut> findAllRequests(@RequestHeader(header) Long userId) {
        return itemRequestService.findAllRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoOut findRequestById(@PathVariable Long requestId) {
        return itemRequestService.findRequestById(requestId);
    }
}
