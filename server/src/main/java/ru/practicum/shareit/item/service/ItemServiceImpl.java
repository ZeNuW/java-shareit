package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingShort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ObjectNotExistException;
import ru.practicum.shareit.exception.ObjectValidationException;
import ru.practicum.shareit.item.dto.ItemWithBookings;
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
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public ItemDto addItem(long userId, ItemDto itemDto) {
        User owner = checkUserExist(userId);
        return ItemMapper.itemToDto(itemRepository.save(ItemMapper.itemFromDto(itemDto, owner)));
    }

    @Override
    @Transactional
    public ItemDto updateItem(long userId, ItemDto itemDto, long itemId) {
        checkUserExist(userId);
        itemRepository.updateItem(itemId, itemDto.getDescription(), itemDto.getAvailable(), itemDto.getName());
        return ItemMapper.itemToDto(itemRepository.getReferenceById(itemId));
    }

    @Override
    public ItemWithBookings getItem(long itemId, long userId) {
        LocalDateTime now = LocalDateTime.now();
        Item item = checkItemExist(itemId);
        List<CommentDto> commentsDto = commentRepository.findAllByItem_IdIn(List.of(item.getId())).stream()
                .map(CommentMapper::commentToDto).collect(Collectors.toList());
        ItemWithBookings itemWithBookings = ItemMapper.itemToItemWithBookings(item, commentsDto);
        if (item.getOwner().getId() == userId) {
            List<BookingShort> bookings =
                    bookingRepository.getNextAndLastItemBooking(List.of(itemId), now);
            for (BookingShort booking : bookings) {
                if (booking.getStartOfBooking().isBefore(now)) {
                    itemWithBookings.setLastBooking(booking);
                } else {
                    itemWithBookings.setNextBooking(booking);
                }
            }
        }
        return itemWithBookings;
    }

    @Override
    public List<ItemWithBookings> getUserItems(long userId, int from, int size) {
        LocalDateTime now = LocalDateTime.now();
        PageRequest page = PageRequest.of(from / size, size);
        List<Item> items = itemRepository.findAllByOwner_Id(userId, page);
        List<Long> itemsId = items.stream().map(Item::getId).collect(Collectors.toList());
        List<Comment> comments = commentRepository.findAllByItem_IdIn(itemsId);
        List<ItemWithBookings> itemsWithBookingsList = ItemMapper.itemToItemWithBookings(items, comments);
        Map<Long, ItemWithBookings> itemsWithBookings = itemsWithBookingsList.stream()
                .collect(Collectors.toMap(ItemWithBookings::getId, Function.identity()));
        List<Long> itemIds = new ArrayList<>(itemsWithBookings.keySet());
        List<BookingShort> bookings = bookingRepository.getNextAndLastItemBooking(itemIds, now);
        for (BookingShort bookingShort : bookings) {
            if (bookingShort.getStartOfBooking().isBefore(now)) {
                itemsWithBookings.get(bookingShort.getItem()).setLastBooking(bookingShort);
            } else {
                itemsWithBookings.get(bookingShort.getItem()).setNextBooking(bookingShort);
            }
        }
        return new ArrayList<>(itemsWithBookings.values());
    }

    @Override
    public List<ItemDto> searchItems(String text, int from, int size) {
        PageRequest page = PageRequest.of(from / size, size);
        return itemRepository.findAllByDescriptionContainingIgnoreCaseAndAvailableIsTrue(text, page).stream()
                .map(ItemMapper::itemToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        List<Booking> bookings = bookingRepository.findAllByBookerAndItemIdAndStatusAndEndOfBookingIsBefore(
                userId, itemId, BookingStatus.APPROVED, LocalDateTime.now());
        if (bookings.isEmpty()) {
            throw new ObjectValidationException("Вы не можете оставить комментарий.");
        }
        User user = checkUserExist(userId);
        Item item = checkItemExist(itemId);
        Comment comment = CommentMapper.commentFromDto(commentDto, user, item);
        comment.setCreated(LocalDateTime.now());
        return CommentMapper.commentToDto(commentRepository.save(comment));
    }

    private User checkUserExist(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new ObjectNotExistException(String.format("Пользователь с id: %d не найден.", userId)));
    }

    private Item checkItemExist(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ObjectNotExistException(String.format("Предмет с id: %d не существует", itemId)));
    }
}