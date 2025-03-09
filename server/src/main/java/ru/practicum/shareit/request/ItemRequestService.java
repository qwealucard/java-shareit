package ru.practicum.shareit.request;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.user.User;

import java.util.List;

@Service
public interface ItemRequestService{
    ItemRequestDtoOut addRequest(ItemRequestDto itemRequestDto, Long id);

    List<ItemRequestDtoOut> getRequests(Long id);

    List<ItemRequestDtoOut> findAllRequests(Long id);

    ItemRequestDtoOut findRequestById(Long id);
}
