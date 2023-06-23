package ru.practicum.shareit.integration;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ObjectNotExistException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemShort;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class BookingServiceIntegrationTest {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingService bookingService;
    private final EasyRandom easyRandom = new EasyRandom();

    @Test
    public void createBookingTest() {
        User user = createUser();
        Item item = createItem(user);
        User booker = createUser();
        BookingDto bookingDto = createBookingDto(item);
        //userId = ownerId
        assertThrows(ObjectNotExistException.class,
                () -> bookingService.createBooking(bookingDto, user.getId()));
        //ok
        BookingDto createdBooking = bookingService.createBooking(bookingDto, booker.getId());
        assertThat(createdBooking).isNotNull();
        assertThat(createdBooking.getId()).isNotNull();
        assertThat(createdBooking.getBooker().getId()).isEqualTo(booker.getId());
    }

    @Test
    public void considerBookingTest() {
        User user = createUser();
        Item item = createItem(user);
        Booking booking = createBooking(user, item.getId(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        booking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(booking);
        booking.setItem(item);
        BookingDto updatedBooking = bookingService.considerBooking(true, booking.getId(), booking.getItem().getOwner().getId());
        assertThat(updatedBooking).isNotNull();
        assertThat(updatedBooking.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    public void getBookingTest() {
        User user = createUser();
        Item item = createItem(user);
        Booking booking = createBooking(user, item.getId(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        bookingRepository.save(booking);
        BookingDto retrievedBooking = bookingService.getBooking(booking.getId(), booking.getBooker());
        assertThat(retrievedBooking).isNotNull();
        assertThat(retrievedBooking.getId()).isEqualTo(booking.getId());
    }

    @Test
    public void getUserBookingsTest() {
        User user = createUser();
        List<BookingDto> bookings = bookingService.getUserBookings("ALL", user.getId(), 0, 10);
        assertThat(bookings).isNotNull();
    }

    @Test
    public void getUserItemBookingsTest() {
        User user = createUser();
        List<BookingDto> bookings = bookingService.getUserItemBookings("ALL", user.getId(), 0, 10);
        assertThat(bookings).isNotNull();
    }

    private Item createItem(User owner) {
        ItemDto itemDto = easyRandom.nextObject(ItemDto.class);
        itemDto.setId(null);
        itemDto.setRequestId(null);
        return itemRepository.save(ItemMapper.itemFromDto(itemDto, owner));
    }

    private User createUser() {
        User user = new User();
        user.setName("John Test");
        user.setEmail("john.test@example.com");
        return userRepository.save(user);
    }

    private Booking createBooking(User booker, long itemId, LocalDateTime start, LocalDateTime end) {
        Booking booking = new Booking();
        booking.setBooker(booker.getId());
        booking.setItem(itemRepository.getReferenceById(itemId));
        booking.setStartOfBooking(start);
        booking.setEndOfBooking(end);
        booking.setStatus(BookingStatus.APPROVED);
        return booking;
    }

    private BookingDto createBookingDto(Item item) {
        BookingDto bookingDto = easyRandom.nextObject(BookingDto.class);
        bookingDto.setId(null);
        bookingDto.setItem(new ItemShort(item.getId(), item.getName()));
        bookingDto.setStatus(BookingStatus.WAITING);
        bookingDto.setStartOfBooking(LocalDateTime.now().plusDays(1));
        bookingDto.setEndOfBooking(LocalDateTime.now().plusDays(3));
        return bookingDto;
    }
}
