package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class ItemRequestServiceImpl implements ItemRequestService {
    UserRepository userRepository;
    ItemRepository itemRepository;
    ItemRequestRepository itemRequestRepository;

    @Autowired
    public ItemRequestServiceImpl(UserRepository userRepository, ItemRepository itemRepository,
                                  ItemRequestRepository itemRequestRepository) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.itemRequestRepository = itemRequestRepository;
    }

    @Override
    public ItemRequestDtoOut addRequest(ItemRequestDto itemRequestDto, Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> {
            log.error("Пользователь с id {} не найден", id);
            return new NotFoundException("Пользователь не найден");
        });
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setRequester(user);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequestRepository.save(itemRequest);
        return ItemRequestMapper.itemRequestDtoOut(itemRequest, null);
    }

    @Override
    public List<ItemRequestDtoOut> getRequests(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(()->{
            log.error("Пользователь с Id {} не найден", userId);
            return new NotFoundException("Пользователь не найден");
        });
        List<ItemRequest> itemRequests = itemRequestRepository.findByRequesterOrderByCreatedDesc(user);
        List<ItemRequestDtoOut> itemRequestDtoOuts = new ArrayList<>();
        for(ItemRequest item : itemRequests) {
            itemRequestDtoOuts.add(ItemRequestMapper.itemRequestDtoOut(item, null));
        }
        return itemRequestDtoOuts;
    }

    @Override
    public List<ItemRequestDtoOut> findAllRequests(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->{
            log.error("Пользователь с Id{} не найден", userId);
            return new NotFoundException("Пользователь не найден");
        });
        List<ItemRequestDtoOut> anotherItems = new ArrayList<>();
        List<ItemRequest> allItems = itemRequestRepository.findAll();
        for(ItemRequest item : allItems) {
            if(item.getRequester() != user) {
                anotherItems.add(ItemRequestMapper.itemRequestDtoOut(item, null));
            }
        }
        return anotherItems;
    }

    @Override
    public ItemRequestDtoOut findRequestById(Long id) {
      ItemRequest itemRequest =  itemRequestRepository.findById(id).orElseThrow(() -> {
           log.error("Запроса с Id {} не существует", id);
          return new NotFoundException("Запрос не найден");
       });
        List<Item> items = itemRepository.findItemsByRequestId(id);
        List<ItemDto> itemRequestsDtoOut = items.stream()
                                                .map(ItemMapper::toItemDto)
                                                .toList();
        return ItemRequestMapper.itemRequestDtoOut(itemRequest, itemRequestsDtoOut);
    }


}
