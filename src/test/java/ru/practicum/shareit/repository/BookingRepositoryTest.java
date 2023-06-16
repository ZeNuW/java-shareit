package ru.practicum.shareit.repository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingShort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    private static Item item;
    private static User user;

    @BeforeAll
    static void setUp() {
        user = new User(1L, "john.doe@example.com", "John Doe");
        item = new Item();
        item.setId(1L);
        item.setName("Item 1");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(user);
    }

    @Test
    public void testGetBooking() {
        userRepository.save(user);
        itemRepository.save(item);
        LocalDateTime nowTime = LocalDateTime.now();
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(1L);
        booking.setStartOfBooking(nowTime.plusDays(1));
        booking.setEndOfBooking(nowTime.plusDays(2));
        entityManager.persist(booking);
        entityManager.flush();
        Booking result = bookingRepository.getBooking(booking.getId(), 1L);
        assertThat(result).isEqualTo(booking);
    }

    @Test
    public void testGetUserBookings() {
        userRepository.save(user);
        itemRepository.save(item);
        LocalDateTime nowTime = LocalDateTime.now();
        Booking booking1 = new Booking();
        booking1.setItem(item);
        booking1.setBooker(1L);
        booking1.setStatus(BookingStatus.APPROVED);
        booking1.setStartOfBooking(nowTime.minusHours(2));
        booking1.setEndOfBooking(nowTime.plusHours(2));
        entityManager.persist(booking1);
        Booking booking2 = new Booking();
        booking2.setItem(item);
        booking2.setBooker(1L);
        booking2.setStatus(BookingStatus.WAITING);
        booking2.setStartOfBooking(nowTime.plusHours(4));
        booking2.setEndOfBooking(nowTime.plusHours(6));
        entityManager.persist(booking2);
        entityManager.flush();
        List<Booking> bookings = bookingRepository.getUserBookings("ALL", 1L, nowTime, PageRequest.of(0, 10));
        assertThat(bookings).hasSize(2);
        assertThat(bookings).containsExactlyInAnyOrder(booking1, booking2);
    }

    @Test
    public void testGetUserItemBookings() {
        userRepository.save(user);
        itemRepository.save(item);
        LocalDateTime nowTime = LocalDateTime.now();
        Booking booking1 = new Booking();
        booking1.setItem(item);
        booking1.setBooker(1L);
        booking1.setStatus(BookingStatus.APPROVED);
        booking1.setStartOfBooking(nowTime.minusHours(2));
        booking1.setEndOfBooking(nowTime.plusHours(2));
        entityManager.persist(booking1);
        Booking booking2 = new Booking();
        booking2.setItem(item);
        booking2.setBooker(1L);
        booking2.setStatus(BookingStatus.WAITING);
        booking2.setStartOfBooking(nowTime.plusHours(4));
        booking2.setEndOfBooking(nowTime.plusHours(6));
        entityManager.persist(booking2);
        entityManager.flush();
        List<Booking> bookings = bookingRepository.getUserItemBookings("ALL", 1L, nowTime, PageRequest.of(0, 10));
        assertThat(bookings).hasSize(2);
        assertThat(bookings).containsExactlyInAnyOrder(booking1, booking2);
    }

    @Test
    public void testGetNextAndLastItemBooking() {
        userRepository.save(user);
        itemRepository.save(item);
        LocalDateTime nowTime = LocalDateTime.now();
        Booking booking1 = new Booking();
        booking1.setItem(item);
        booking1.setBooker(1L);
        booking1.setStatus(BookingStatus.APPROVED);
        booking1.setStartOfBooking(nowTime.plusHours(4));
        booking1.setEndOfBooking(nowTime.plusHours(6));
        entityManager.persist(booking1);
        Booking booking2 = new Booking();
        booking2.setItem(item);
        booking2.setBooker(1L);
        booking2.setStatus(BookingStatus.WAITING);
        booking2.setStartOfBooking(nowTime.plusDays(3));
        booking2.setEndOfBooking(nowTime.plusDays(4));
        entityManager.persist(booking2);
        entityManager.flush();
        List<BookingShort> bookings = bookingRepository.getNextAndLastItemBooking(
                Collections.singletonList(booking1.getItem().getId()), nowTime);
        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getId()).isEqualTo(booking1.getId());
    }

    @Test
    public void testFindAllByBookerAndItemIdAndStatusAndEndOfBookingIsBefore() {
        userRepository.save(user);
        itemRepository.save(item);
        LocalDateTime nowTime = LocalDateTime.now();
        Booking booking1 = new Booking();
        booking1.setItem(item);
        booking1.setBooker(1L);
        booking1.setStatus(BookingStatus.APPROVED);
        booking1.setStartOfBooking(nowTime.minusDays(2));
        booking1.setEndOfBooking(nowTime.minusDays(1));
        entityManager.persist(booking1);
        Booking booking2 = new Booking();
        booking2.setItem(item);
        booking2.setBooker(1L);
        booking2.setStatus(BookingStatus.WAITING);
        booking2.setStartOfBooking(nowTime.plusDays(3));
        booking2.setEndOfBooking(nowTime.plusDays(4));
        entityManager.persist(booking2);
        entityManager.refresh(booking1);
        entityManager.refresh(booking2);
        List<Booking> bookings = bookingRepository.findAllByBookerAndItemIdAndStatusAndEndOfBookingIsBefore(
                1L, booking1.getItem().getId(), BookingStatus.APPROVED, nowTime);
        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0)).isEqualTo(booking1);
    }
}
