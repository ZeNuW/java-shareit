package ru.practicum.shareit.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.ObjectNotExistException;
import ru.practicum.shareit.exception.ObjectValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceUnitTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private BookingServiceImpl bookingService;

    private final User owner = new User(1L, "test@test.com", "name");
    private final User user = new User(2L, "user@test.com", "user");
    private final ItemDto itemDto = new ItemDto(1L, "описание", "имя", true, 1L);
    private Booking booking;
    private Item item;
    private BookingDto bookingDto;
    private LocalDateTime nowTime;

    @BeforeEach
    public void setUp() {
        nowTime = LocalDateTime.now();
        item = ItemMapper.itemFromDto(itemDto, owner);
        booking = new Booking(1L, BookingStatus.WAITING, nowTime.minusDays(1),
                nowTime.plusDays(1), item, 2L);
        bookingDto = BookingMapper.bookingToDto(booking);
        bookingDto.setStartOfBooking(nowTime.plusHours(1));
        bookingDto.setEndOfBooking(nowTime.plusHours(2));
    }

    @Test
    void createBookingReturnsBookingDto() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        BookingDto createdBooking = bookingService.createBooking(bookingDto, user.getId());
        assertNotNull(createdBooking);
        assertEquals(BookingStatus.WAITING, createdBooking.getStatus());
        assertEquals(user.getId(), createdBooking.getBooker().getId());
        verify(userRepository, times(1)).findById(user.getId());
        verify(itemRepository, times(1)).findById(bookingDto.getItem().getId());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void createBookingThrowsObjectNotExistException() {
        //user throw
        assertThrows(ObjectNotExistException.class, () -> bookingService.createBooking(bookingDto, user.getId()));
        verify(userRepository, times(1)).findById(user.getId());
        verifyNoInteractions(bookingRepository);
        //item throw
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenThrow(ObjectNotExistException.class);
        assertThrows(ObjectNotExistException.class, () -> bookingService.createBooking(bookingDto, user.getId()));
    }

    @Test
    void createBookingThrowsObjectValidationException() {
        when(itemRepository.findById(bookingDto.getItem().getId())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        //end раньше now
        bookingDto.setStartOfBooking(nowTime);
        bookingDto.setEndOfBooking(nowTime.minusDays(1));
        assertThrows(ObjectValidationException.class, () -> bookingService.createBooking(bookingDto, user.getId()));
        verify(userRepository).findById(user.getId());
        verify(itemRepository).findById(bookingDto.getItem().getId());
        //end раньше start
        bookingDto.setStartOfBooking(nowTime.plusDays(2));
        bookingDto.setEndOfBooking(nowTime.plusDays(1));
        assertThrows(ObjectValidationException.class, () -> bookingService.createBooking(bookingDto, user.getId()));
        verify(userRepository, times(2)).findById(user.getId());
        verify(itemRepository, times(2)).findById(bookingDto.getItem().getId());
        //start = end
        bookingDto.setStartOfBooking(nowTime);
        bookingDto.setEndOfBooking(nowTime);
        assertThrows(ObjectValidationException.class, () -> bookingService.createBooking(bookingDto, user.getId()));
        verify(userRepository, times(3)).findById(user.getId());
        verify(itemRepository, times(3)).findById(bookingDto.getItem().getId());
        //start раньше now
        bookingDto.setStartOfBooking(nowTime.minusDays(1));
        bookingDto.setEndOfBooking(nowTime.plusDays(1));
        assertThrows(ObjectValidationException.class, () -> bookingService.createBooking(bookingDto, user.getId()));
        verify(userRepository, times(4)).findById(user.getId());
        verify(itemRepository, times(4)).findById(bookingDto.getItem().getId());
        verifyNoInteractions(bookingRepository);
    }

    @Test
    void considerBookingReturnBookingDto() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingRepository.saveAndFlush(any(Booking.class))).thenReturn(booking);
        BookingDto consideredBooking = bookingService.considerBooking(true, booking.getId(), owner.getId());
        assertNotNull(consideredBooking);
        assertEquals(BookingStatus.APPROVED, consideredBooking.getStatus());
        verify(bookingRepository).findById(booking.getId());
        verify(bookingRepository).saveAndFlush(any(Booking.class));
    }

    @Test
    void considerBookingThrowsObjectNotExistException() {
        //not owner
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        assertThrows(ObjectNotExistException.class,
                () -> bookingService.considerBooking(true, booking.getId(), user.getId()));
        verify(bookingRepository, times(1)).findById(booking.getId());
        //booking not exist
        when(bookingRepository.findById(anyLong())).thenThrow(ObjectNotExistException.class);
        assertThrows(ObjectNotExistException.class,
                () -> bookingService.considerBooking(true, booking.getId(), owner.getId()));
        verify(bookingRepository, times(2)).findById(booking.getId());
    }

    @Test
    void getBookingReturnBookingDto() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.getBooking(anyLong(),anyLong())).thenReturn(booking);
        BookingDto retrievedBooking = bookingService.getBooking(booking.getId(), user.getId());
        assertNotNull(retrievedBooking);
        assertEquals(booking.getId(), retrievedBooking.getId());
        verify(bookingRepository).findById(booking.getId());
    }

    @Test
    void getBooking_BookingDoesNotExist_ThrowsObjectNotExistException() {
        //no booking
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ObjectNotExistException.class, () -> bookingService.getBooking(booking.getId(), owner.getId()));
        verify(bookingRepository).findById(booking.getId());
        //not owner or booker
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        assertThrows(ObjectNotExistException.class, () -> bookingService.getBooking(booking.getId(), 3L));
        verify(bookingRepository, times(2)).findById(booking.getId());
    }
}
