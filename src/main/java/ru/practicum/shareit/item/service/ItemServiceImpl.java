package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingShort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ObjectNotExistException;
import ru.practicum.shareit.exception.ObjectValidationException;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public ItemDto addItem(long userId, ItemDto itemDto) {
        User owner = userRepository.findById(userId).orElseThrow(
                () -> new ObjectNotExistException("Пользователь с id: " + userId + " не найден"));
        return ItemMapper.itemToDto(itemRepository.save(ItemMapper.itemFromDto(itemDto, owner)));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public ItemDto updateItem(long userId, ItemDto itemDto, long itemId) {
        userRepository.findById(userId).orElseThrow(
                () -> new ObjectNotExistException("Пользователь с id: " + userId + " не найден"));
        itemRepository.updateItem(itemId, itemDto.getDescription(), itemDto.getAvailable(), itemDto.getName());
        return ItemMapper.itemToDto(itemRepository.getReferenceById(itemId));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public ItemDto getItem(long itemId, long userId) {
        LocalDateTime now = LocalDateTime.now();
        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new ObjectNotExistException("Предмет с id: " + itemId + " не существует"));
        ItemDto itemDto = ItemMapper.itemToDto(item);
        if (item.getOwner().getId() == userId) {
            List<BookingShort> bookings = bookingRepository.getNextAndLastItemBooking(itemId, now);
            for (BookingShort booking : bookings) {
                if (booking.getStartOfBooking().isBefore(now)) {
                    itemDto.setLastBooking(booking);
                } else {
                    itemDto.setNextBooking(booking);
                }
            }
        }
        return itemDto;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<ItemDto> getUserItems(long userId) {
        /*
        не получилось у меня избежать цикличных запросов в БД, все попытки сделать запрос всех аренд по времени
         в 1 sql запросе были неудачными
         */
        LocalDateTime now = LocalDateTime.now();
        return itemRepository.findAllByOwner_Id(userId).stream()
                .sorted(Comparator.comparingLong(Item::getId))
                .map(ItemMapper::itemToDto)
                .peek(itemDto -> {
                    List<BookingShort> bookings =
                            bookingRepository.getNextAndLastItemBooking(itemDto.getId(), now);
                    for (BookingShort booking : bookings) {
                        if (booking.getStartOfBooking().isBefore(now)) {
                            itemDto.setLastBooking(booking);
                        } else {
                            itemDto.setNextBooking(booking);
                        }
                    }
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<ItemDto> searchItems(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.findAllByDescriptionContainingIgnoreCaseAndAvailableIsTrue(text).stream()
                .map(ItemMapper::itemToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        List<Booking> bookings = bookingRepository.findAllByBookerAndItemIdAndStatusAndEndOfBookingIsBefore(
                userId, itemId, BookingStatus.APPROVED, LocalDateTime.now());
        if (bookings.isEmpty()) {
            throw new ObjectValidationException("Вы не можете оставить комментарий.");
        }
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ObjectNotExistException("Пользователь с id: " + userId + " не найден"));
        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new ObjectNotExistException("Предмет с id: " + itemId + " не существует"));
        Comment comment = CommentMapper.commentFromDto(commentDto, user, item);
        comment.setCreated(LocalDateTime.now());
        return CommentMapper.commentToDto(commentRepository.save(comment));
    }
}