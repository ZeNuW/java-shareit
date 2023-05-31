package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ObjectNotExistException;
import ru.practicum.shareit.exception.ObjectValidationException;
import ru.practicum.shareit.item.dto.ItemShort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserShort;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final EntityManager entityManager;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public BookingDto createBooking(BookingDto bookingDto, Long userId) {
        userRepository.findById(userId).orElseThrow(
                () -> new ObjectNotExistException("Пользователь с id: " + userId + " не найден"));
        bookingDto.setStatus(BookingStatus.WAITING);
        bookingDto.setBooker(new UserShort(userId));
        Item item = itemRepository.findById(bookingDto.getItem().getId()).orElseThrow(() ->
                new ObjectNotExistException("Предмет с id: " + bookingDto.getItem().getId() + " не найден."));
        if (item.getOwner().getId().equals(userId)) {
            throw new ObjectNotExistException("Нельзя арендовать свою же вещь!");
        }
        if (!item.getAvailable()) {
            throw new ObjectValidationException("Предмет " + item.getName() + " недоступен для брони.");
        }
        if (bookingDto.getEndOfBooking().isBefore(LocalDateTime.now())) {
            throw new ObjectValidationException("Время начала брони установлено в прошлом.");
        }
        if (bookingDto.getEndOfBooking().isBefore(bookingDto.getStartOfBooking())) {
            throw new ObjectValidationException("Время окончания брони установлено раньше начала брони.");
        }
        if (bookingDto.getStartOfBooking().equals(bookingDto.getEndOfBooking())) {
            throw new ObjectValidationException("Время начала и конца брони установлено в одно время.");
        }
        if (bookingDto.getStartOfBooking().isBefore(LocalDateTime.now())) {
            throw new ObjectValidationException("Время начала брони установлено в прошлом.");
        }
        return BookingMapper.bookingToDto(bookingRepository.save(
                BookingMapper.bookingFromDto(bookingDto, item)), new ItemShort(item.getId(), item.getName()));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public BookingDto considerBooking(Boolean approved, Long bookingId, Long userId) {
        BookingStatus status = approved ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new ObjectNotExistException("Аренды с id: " + bookingId + " не найдено."));
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new ObjectNotExistException("Вы не являетесь владельцем предмета и не можете сменить статус");
        }
        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new ObjectValidationException("Статус уже был изменён");
        }
        bookingRepository.considerBooking(status.toString(), bookingId, userId);
        entityManager.refresh(booking);
        return BookingMapper.bookingToDto(booking, new ItemShort(
                booking.getItem().getId(), booking.getItem().getName()));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public BookingDto getBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new ObjectNotExistException("Аренды с id: " + bookingId + " не найдено."));
        if (!(booking.getBooker().equals(userId) || booking.getItem().getOwner().getId().equals(userId))) {
            throw new ObjectNotExistException(userId + " не является владельцем предмета или создателем брони");
        }
        return BookingMapper.bookingToDto(bookingRepository.getBooking(bookingId, userId),
                new ItemShort(booking.getItem().getId(), booking.getItem().getName()));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<BookingDto> getUserBookings(String state, Long userId) {
        try {
            BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new ObjectValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
        userRepository.findById(userId).orElseThrow(
                () -> new ObjectNotExistException("Пользователь с id: " + userId + " не найден"));
        return bookingRepository.getUserBookings(state, userId, LocalDateTime.now()).stream()
                .map(b -> BookingMapper.bookingToDto(b,
                        new ItemShort(b.getItem().getId(), b.getItem().getName())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<BookingDto> getUserItemBookings(String state, Long userId) {
        try {
            BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new ObjectValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
        userRepository.findById(userId).orElseThrow(
                () -> new ObjectNotExistException("Пользователь с id: " + userId + " не найден"));
        return bookingRepository.getUserItemBookings(state, userId, LocalDateTime.now()).stream()
                .map(b -> BookingMapper.bookingToDto(b,
                        new ItemShort(b.getItem().getId(), b.getItem().getName())))
                .collect(Collectors.toList());
    }
}