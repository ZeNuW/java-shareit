package ru.practicum.shareit.unit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ObjectNotExistException;
import ru.practicum.shareit.exception.ObjectValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookings;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceUnitTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private final User owner = new User(1L, "test@test.com", "name");
    private final ItemDto itemDto = new ItemDto(1L, "описание", "имя", true, 1L);

    @Test
    void addItemReturnsItemDto() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.save(any(Item.class))).thenReturn(ItemMapper.itemFromDto(itemDto, owner));
        ItemDto result = itemService.addItem(owner.getId(), itemDto);
        assertEquals(itemDto, result);
        verify(userRepository, times(1)).findById(eq(owner.getId()));
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    public void addItemThrowsObjectNotExistException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ObjectNotExistException.class, () -> itemService.addItem(owner.getId(), itemDto));
        verify(userRepository, times(1)).findById(owner.getId());
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    public void updateItemReturnsItemDto() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.getReferenceById(anyLong())).thenReturn(ItemMapper.itemFromDto(itemDto, owner));
        ItemDto result = itemService.updateItem(owner.getId(), itemDto, itemDto.getId());
        assertNotNull(result);
        verify(userRepository, times(1)).findById(owner.getId());
        verify(itemRepository, times(1))
                .updateItem(itemDto.getId(), itemDto.getDescription(), itemDto.getAvailable(), itemDto.getName());
        verify(itemRepository, times(1)).getReferenceById(itemDto.getId());
    }

    @Test
    public void updateItemThrowsObjectNotExistException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ObjectNotExistException.class,
                () -> itemService.updateItem(owner.getId(), itemDto, itemDto.getId()));
        verify(userRepository, times(1)).findById(owner.getId());
        verify(itemRepository, never()).updateItem(anyLong(), anyString(), anyBoolean(), anyString());
        verify(itemRepository, never()).getReferenceById(anyLong());
    }

    @Test
    public void getItemReturnsItemWithBookings() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(ItemMapper.itemFromDto(itemDto, owner)));
        when(bookingRepository
                .getNextAndLastItemBooking(anyList(), any(LocalDateTime.class))).thenReturn(new ArrayList<>());
        ItemWithBookings result = itemService.getItem(itemDto.getId(), owner.getId());
        assertNotNull(result);
        verify(itemRepository, times(1)).findById(itemDto.getId());
        verify(bookingRepository, times(1))
                .getNextAndLastItemBooking(anyList(), any(LocalDateTime.class));
    }

    @Test
    public void getItemThrowsObjectNotExistException() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ObjectNotExistException.class, () -> itemService.getItem(itemDto.getId(), owner.getId()));
        verify(itemRepository, times(1)).findById(itemDto.getId());
        verify(itemRepository, never()).getReferenceById(anyLong());
        verify(bookingRepository, never()).getNextAndLastItemBooking(anyList(), any(LocalDateTime.class));
    }

    @Test
    public void getUserItemsReturnsListOfItemWithBookings() {
        int from = 0;
        int size = 10;
        when(itemRepository.findAllByOwner_Id(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(ItemMapper.itemFromDto(itemDto, owner)));
        when(bookingRepository.getNextAndLastItemBooking(anyList(), any(LocalDateTime.class)))
                .thenReturn(new ArrayList<>());
        List<ItemWithBookings> result = itemService.getUserItems(owner.getId(), from, size);
        assertNotNull(result);
        verify(itemRepository, times(1)).findAllByOwner_Id(eq(owner.getId()), any());
        verify(bookingRepository, times(1))
                .getNextAndLastItemBooking(anyList(), any(LocalDateTime.class));
    }

    @Test
    public void searchItemsReturnsListOfItemDto() {
        String text = "search text";
        int from = 0;
        int size = 10;
        when(itemRepository.findAllByDescriptionContainingIgnoreCaseAndAvailableIsTrue(eq(text), any(Pageable.class)))
                .thenReturn(List.of(ItemMapper.itemFromDto(itemDto, owner)));
        List<ItemDto> result = itemService.searchItems(text, from, size);
        assertNotNull(result);
        verify(itemRepository, times(1))
                .findAllByDescriptionContainingIgnoreCaseAndAvailableIsTrue(eq(text), any());
    }

    @Test
    public void addCommentReturnsCommentDto() {
        CommentDto commentDto = new CommentDto(1L, "комментарий", "name", LocalDateTime.now());
        Booking booking = new Booking(1L, BookingStatus.APPROVED, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1),
                ItemMapper.itemFromDto(itemDto, owner), owner.getId());
        when(bookingRepository.findAllByBookerAndItemIdAndStatusAndEndOfBookingIsBefore(
                anyLong(), anyLong(), any(BookingStatus.class), any(LocalDateTime.class))).thenReturn(List.of(booking));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.findById(any())).thenReturn(Optional.of(ItemMapper.itemFromDto(itemDto, owner)));
        when(commentRepository.save(any(Comment.class))).thenReturn(CommentMapper.commentFromDto(commentDto, owner,
                ItemMapper.itemFromDto(itemDto, owner)));
        CommentDto result = itemService.addComment(owner.getId(), itemDto.getId(), commentDto);
        assertNotNull(result);
        verify(bookingRepository, times(1)).findAllByBookerAndItemIdAndStatusAndEndOfBookingIsBefore(
                eq(owner.getId()), eq(itemDto.getId()), eq(BookingStatus.APPROVED), any(LocalDateTime.class));
        verify(userRepository, times(1)).findById(owner.getId());
        verify(itemRepository, times(1)).findById(itemDto.getId());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    public void addCommentThrowsObjectValidationException() {
        CommentDto commentDto = new CommentDto(1L, "комментарий", "name", LocalDateTime.now());
        List<Booking> bookings = new ArrayList<>();
        when(bookingRepository.findAllByBookerAndItemIdAndStatusAndEndOfBookingIsBefore(
                anyLong(), anyLong(), any(BookingStatus.class), any(LocalDateTime.class))).thenReturn(bookings);
        assertThrows(ObjectValidationException.class, () -> itemService.addComment(owner.getId(), itemDto.getId(), commentDto));
        verify(bookingRepository, times(1)).findAllByBookerAndItemIdAndStatusAndEndOfBookingIsBefore(
                eq(owner.getId()), eq(itemDto.getId()), eq(BookingStatus.APPROVED), any(LocalDateTime.class));
        verify(userRepository, never()).findById(anyLong());
        verify(itemRepository, never()).findById(anyLong());
        verify(commentRepository, never()).save(any(Comment.class));
    }
}