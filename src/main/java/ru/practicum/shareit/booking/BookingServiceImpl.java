package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemService itemService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Autowired
    BookingServiceImpl(BookingRepository bookingRepository, ItemService itemService, UserService userService,
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
            log.error("Вещь с ID {} недоступна для бронирования", item.getId());
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

        Optional<Booking> bookingByBooker = bookingRepository.findByIdAndBookerId(bookingId, userId);
        Optional<Booking> bookingByOwner = bookingRepository.findByIdAndItemOwnerId(bookingId, userId);

        return bookingByBooker.or(() -> bookingByOwner)
                              .map(BookingMapper::toBookingForOut)
                              .orElseThrow(() -> new NotFoundException("Бронирование не найдено."));
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
            } catch (IllegalStatusException e) {
                log.error("Статус {} неизвестен ", state);
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
