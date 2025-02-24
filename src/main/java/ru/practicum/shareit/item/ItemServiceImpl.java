package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentOutDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithCommentsDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final UserService userService;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserService userService,
                           UserRepository userRepository, BookingRepository bookingRepository,
                           CommentRepository commentRepository) {
        this.userService = userService;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    @Transactional
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            log.error("Попытка добавить вещь с пустым названием");
            throw new ValidateException("Название вещи не заполнено");
        }

        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            log.error("Попытка добавить вещь с пустым описанием");
            throw new ValidateException("Описание не заполнено");
        }
        if (itemDto.getAvailable() == null) {
            log.error("Попытка добавить вещь без указания статуса available");
            throw new ValidateException("Статус аренды не указан");
        }
        User owner = userRepository.findById(userId)
                                   .orElseThrow(() -> {
                                       log.error("Владелец с ID {} не найден при добавлении вещи", userId);
                                       return new NotFoundException("Владелец не найден");
                                   });
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner);
        Item addedItem = itemRepository.save(item);
        log.info("Вещь с ID {} создана", addedItem.getId());
        return ItemMapper.toItemDto(addedItem);
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long userId, ItemDto updatedItemDto) {
        Item existingItem = itemRepository.findById(updatedItemDto.getId())
                                          .orElseThrow(() -> {
                                              log.error("Вещь с ID {} не найдена при обновлении", updatedItemDto.getId());
                                              return new NotFoundException("Вещь не найдена");
                                          });
        try {
            User owner = userService.findUserById(userId);
            if (!existingItem.getOwner().getId().equals(userId)) {
                log.warn("Пользователь с ID {} пытается обновить чужую вещь с ID {}", userId, updatedItemDto.getId());
                throw new ForbiddenException("Вы не владелец этой вещи");
            }
            if (updatedItemDto.getName() != null) {
                existingItem.setName(updatedItemDto.getName());
                log.info("Имя вещи с ID {} обновлено на {}", updatedItemDto.getId(), updatedItemDto.getName());
            }
            if (updatedItemDto.getDescription() != null) {
                existingItem.setDescription(updatedItemDto.getDescription());
                log.info("Описание вещи с ID {} обновлено", updatedItemDto.getId());
            }
            if (updatedItemDto.getAvailable() != null) {
                existingItem.setAvailable(updatedItemDto.getAvailable());
                log.info("Статус available вещи с ID {} обновлен", updatedItemDto.getId());
            }
            Item updatedItem = itemRepository.save(existingItem);
            log.info("Вещь с ID {} обновлена", updatedItemDto.getId());
            return ItemMapper.toItemDto(updatedItem);
        } catch (NotFoundException e) {
            log.error("Владелец с ID {} не найден при обновлении вещи", userId, e);
            throw new NotFoundException("Владелец не найден");
        }
    }

    @Override
    public ItemDto findItemDtoById(Long itemId) {
        Item item = itemRepository.findById(itemId)
                                  .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = bookingRepository.findByItemIdOrderByStartDesc(itemId);

        BookingForItemDto lastBooking = null;
        BookingForItemDto nextBooking = null;

        if (!bookings.isEmpty()) {

            List<Booking> validBookings = bookings.stream()
                                                  .filter(Objects::nonNull)
                                                  .filter(booking -> booking.getStatus() == Status.APPROVED)
                                                  .toList();

            Optional<Booking> lastBookingOptional = validBookings.stream()
                                                                 .filter(booking -> booking.getStart().isBefore(now))
                                                                 .max(Comparator.comparing(Booking::getEnd));

            if (lastBookingOptional.isPresent()) {
                lastBooking = BookingMapper.toBookingForItemDto(lastBookingOptional.get());
            }

            Optional<Booking> nextBookingOptional = validBookings.stream()
                                                                 .filter(booking -> booking.getStart().isAfter(now))
                                                                 .min(Comparator.comparing(Booking::getStart));

            if (nextBookingOptional.isPresent()) {
                nextBooking = BookingMapper.toBookingForItemDto(nextBookingOptional.get());
            }
        }

        ItemDto itemDto = ItemMapper.toItemDto(item);
        itemDto.setLastBooking(lastBooking);
        itemDto.setNextBooking(nextBooking);
        return itemDto;
    }

    @Override
    public ItemWithCommentsDto findItemDtoWithCommentsById(Long id) {

        Item item = itemRepository.findById(id)
                                  .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        ItemDto itemDto = ItemMapper.toItemDto(item);

        List<Comment> comments = commentRepository.findByItem(item);

        List<CommentOutDto> commentsDto = new ArrayList<>();
        for (Comment comment : comments) {
            CommentOutDto commentOutDto = CommentMapper.commentOutDto(comment);
            commentsDto.add(commentOutDto);
        }
        return ItemMapper.toItemWithCommentsDto(itemDto, commentsDto);
    }

    @Override
    public List<ItemWithCommentsDto> userItems(Long userId) {
        List<Item> items = itemRepository.findItemsByOwnerId(userId);
        return items.stream()
                    .map(item -> {
                        ItemDto itemDto = ItemMapper.toItemDto(item);
                        List<Comment> comments = commentRepository.findByItem(item);
                        List<CommentOutDto> commentsDto = new ArrayList<>();
                        for (Comment comment : comments) {
                            CommentOutDto commentOutDto = CommentMapper.commentOutDto(comment);
                            commentsDto.add(commentOutDto);
                        }
                        return ItemMapper.toItemWithCommentsDto(itemDto, commentsDto);
                    })
                    .collect(Collectors.toList());
    }

    private List<Item> searchAvailableItems(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.findAll().stream()
                             .filter(item -> (item.getName().toLowerCase().contains(text.toLowerCase()) ||
                                     item.getDescription().toLowerCase().contains(text.toLowerCase())) &&
                                     item.isAvailable())
                             .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        return searchAvailableItems(text).stream()
                                         .map(ItemMapper::toItemDto)
                                         .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public CommentOutDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        log.info("Добавляем отзыв");
        User user = userRepository.findById(userId)
                                  .orElseThrow(() -> {
                                      log.error("Пользователь с ID {} не найден", userId);
                                      return new NotFoundException("Пользователь не найден");
                                  });
        Item item = itemRepository.findById(itemId)
                                  .orElseThrow(() -> {
                                      log.error("Вещь с ID {} не найдена", itemId);
                                      return new NotFoundException("Вещь не найдена");
                                  });
        if (!item.isAvailable()) {
            log.error("Вы не арендовали эту вещь с ID{}", itemId);
            throw new BookingNotAvailableItemsException("Вы не бронировали эту вещь");
        }
        List<Booking> bookings = bookingRepository.findByItemIdAndBookerIdAndStatusAndEndBefore(
                itemId, userId, Status.APPROVED, LocalDateTime.now());

        if (bookings.isEmpty()) {
            log.error("Пользователь с ID {} не имеет завершенных бронирований для вещи с ID {} или бронирования не подтверждены", userId, itemId);
            throw new CommentNotBookingItemException("Вы не можете оставить комментарий, так как у вас нет завершенных и одобренных бронирований для этой вещи.");
        }

        Comment comment = CommentMapper.toComment(commentDto, item, user);
        comment.setCreated(LocalDateTime.now());
        Comment savedComment = commentRepository.save(comment);
        return CommentMapper.commentOutDto(savedComment);
    }
}
