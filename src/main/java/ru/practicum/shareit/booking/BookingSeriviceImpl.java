package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.exceptions.BookingNotAvailableItemsException;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidateException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class BookingSeriviceImpl implements BookingService {

    BookingRepository bookingRepository;
    ItemService itemService;
    UserService userService;
    UserRepository userRepository;
    ItemRepository itemRepository;

    @Autowired
    BookingSeriviceImpl(BookingRepository bookingRepository, ItemService itemService, UserService userService,
                        UserRepository userRepository, ItemRepository itemRepository) {
        this.bookingRepository = bookingRepository;
        this.itemService = itemService;
        this.userService = userService;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    @Transactional
    public BookingDtoOut addBooking(Long userId, BookingDto bookingDto) {
        log.info("Создаем бронь");
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() -> {
            log.error("Вещь с ID {} не найдена", bookingDto.getItemId());
            return new NotFoundException("Вещь не найдена");
        });

        if (!item.isAvailable()) {
            throw new BookingNotAvailableItemsException("Вещь недоступна для бронирования.");
        }
        if (bookingDto.getStart().isBefore(LocalDateTime.now()) || bookingDto.getEnd().isBefore(LocalDateTime.now()) || !bookingDto.getEnd().isAfter(bookingDto.getStart())) {
            throw new ValidateException("Некорректные даты бронирования.");
        }
        User booker = userRepository.findById(userId)
                                    .orElseThrow(() -> {
                                        log.error("Пользователь с ID {} не найден", userId);
                                        return new NotFoundException("Пользователь не найден");
                                    });
        if (booker == null) {
            throw new NotFoundException("Пользователь не найден.");
        }

        Booking booking = BookingMapper.toBooking(bookingDto, item, booker);
        Booking savedBooking = bookingRepository.save(booking);
        return BookingMapper.toBookingForOut(savedBooking);
    }

    @Override
    @Transactional
    public BookingDtoOut approveBooking(Long bookingId, Long userId, Boolean approved) {
        log.info("Устанавливаем approved брони");
        Booking booking = bookingRepository.findById(bookingId)
                                           .orElseThrow(() -> new NotFoundException("Бронирование не найдено."));

        ItemDto itemDto = itemService.findItemDtoById(booking.getItem().getId());

        if (!itemDto.getOwner().equals(userId)) {
            throw new ForbiddenException("Только владелец вещи может подтвердить/отклонить бронирование.");
        }
        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return BookingMapper.toBookingForOut(bookingRepository.save(booking));
    }

    @Override
    public BookingDtoOut getBookingById(Long bookingId, Long userId) {
        log.info("Получаем бронь по id");
        Booking booking = bookingRepository.findByIdAndBookerId(bookingId, userId)
                                           .orElseGet(() -> bookingRepository.findByIdAndItemOwnerId(bookingId, userId)
                                                                             .orElseThrow(() -> new NotFoundException("Бронирование не найдено.")));

        return BookingMapper.toBookingForOut(booking);
    }

    @Override
    public List<BookingDtoOut> getBookings(Long userId, String state) {
        log.info("Ищем брони владельца");
        userService.findUserById(userId);

        List<Booking> bookings;

        if (state == null || state.equalsIgnoreCase("ALL")) {
            bookings = bookingRepository.findByBookerIdOrderByStartDesc(userId);
        } else {
            try {
                Status status = Status.valueOf(state.toUpperCase());
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, status);
            } catch (IllegalArgumentException e) {
                throw new ValidateException("Неизвестный статус: " + state);
            }
        }

        return bookings.stream()
                       .map(BookingMapper::toBookingForOut)
                       .collect(Collectors.toList());
    }

    @Override
    public List<BookingDtoOut> getOwnerBookings(Long userId, String state) {
        log.info("Ищем owner`s брони");
        userService.findUserById(userId);

        List<Booking> bookings;

        if (state == null || state.equalsIgnoreCase("ALL")) {
            bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(userId);
        } else {
            try {
                Status status = Status.valueOf(state.toUpperCase());
                bookings = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(userId, status);
            } catch (IllegalArgumentException e) {
                throw new ValidateException("Неизвестный статус: " + state);
            }
        }

        return bookings.stream()
                       .map(BookingMapper::toBookingForOut)
                       .collect(Collectors.toList());
    }
}
