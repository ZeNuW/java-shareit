package ru.practicum.shareit.integration;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
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
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class ItemServiceIntegrationTest {

    @Autowired
    private ItemService itemService;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private CommentRepository commentRepository;
    private final EasyRandom easyRandom = new EasyRandom();

    @Test
    public void testAddItem() {
        User owner = createUser();
        ItemDto itemDto = createItemDto();
        ItemDto addedItem = itemService.addItem(owner.getId(), itemDto);
        assertNotNull(addedItem.getId());
        assertEquals(itemDto.getName(), addedItem.getName());
        assertEquals(itemDto.getDescription(), addedItem.getDescription());
    }

    @Test
    public void testUpdateItem() {
        User owner = createUser();
        ItemDto itemDto = createItemDto();
        ItemDto addedItem = itemService.addItem(owner.getId(), itemDto);
        ItemDto updatedItem = itemService.updateItem(owner.getId(), itemDto, addedItem.getId());
        assertEquals(addedItem.getId(), updatedItem.getId());
        assertEquals(itemDto.getName(), updatedItem.getName());
        assertEquals(itemDto.getDescription(), updatedItem.getDescription());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void testGetItem() {
        LocalDateTime nowTime = LocalDateTime.now();
        User owner = createUser();
        User booker = createUser();
        ItemDto itemDto = createItemDto();
        Item addedItem = itemRepository.save(ItemMapper.itemFromDto(itemDto, owner));
        Booking lastBooking = new Booking(1L, BookingStatus.APPROVED, nowTime.minusHours(1), nowTime.plusHours(1),
                addedItem, booker.getId());
        Booking nextBooking = new Booking(2L, BookingStatus.APPROVED, nowTime.plusHours(2), nowTime.plusHours(3),
                addedItem, booker.getId());
        ItemWithBookings itemWithBookings = itemService.getItem(addedItem.getId(), owner.getId());
        assertNotNull(itemWithBookings.getId());
        assertEquals(addedItem.getId(), itemWithBookings.getId());
        assertEquals(itemDto.getName(), itemWithBookings.getName());
        assertEquals(itemDto.getDescription(), itemWithBookings.getDescription());
        //with bookings
        bookingRepository.saveAllAndFlush(List.of(lastBooking, nextBooking));
        itemWithBookings = itemService.getItem(addedItem.getId(), owner.getId());
        assertEquals(lastBooking.getId(), itemWithBookings.getLastBooking().getId());
        assertEquals(nextBooking.getId(), itemWithBookings.getNextBooking().getId());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void testGetUserItems() {
        User owner = createUser();
        User booker = createUser();
        LocalDateTime nowTime = LocalDateTime.now();
        ItemDto item1 = createItemDto();
        ItemDto item2 = createItemDto();
        ItemDto item3 = createItemDto();
        item1 = itemService.addItem(owner.getId(), item1);
        item2 = itemService.addItem(owner.getId(), item2);
        item3 = itemService.addItem(owner.getId(), item3);
        //ex
        assertThrows(ObjectValidationException.class, () -> itemService.searchItems("test", 0, 0));
        //ok
        List<ItemWithBookings> userItems = itemService.getUserItems(owner.getId(), 0, 10);
        assertEquals(3, userItems.size());
        List<Long> itemIds = userItems.stream().map(ItemWithBookings::getId).collect(Collectors.toList());
        assertTrue(itemIds.contains(item1.getId()));
        assertTrue(itemIds.contains(item2.getId()));
        assertTrue(itemIds.contains(item3.getId()));
        //with bookings
        Item item = itemRepository.getReferenceById(item1.getId());
        Booking lastBooking = new Booking(1L, BookingStatus.APPROVED, nowTime.minusHours(1), nowTime.plusHours(1),
                item, booker.getId());
        Booking nextBooking = new Booking(2L, BookingStatus.APPROVED, nowTime.plusHours(2), nowTime.plusHours(3),
                item, booker.getId());
        bookingRepository.saveAllAndFlush(List.of(lastBooking, nextBooking));
        userItems = itemService.getUserItems(owner.getId(), 0, 10);
        for (ItemWithBookings userItem : userItems) {
            System.out.println(userItem);
        }
        assertEquals(lastBooking.getId(), userItems.get(0).getLastBooking().getId());
        assertEquals(nextBooking.getId(), userItems.get(0).getNextBooking().getId());
    }

    @Test
    public void testSearchItems() {
        User owner = createUser();
        ItemDto item1 = createItemDto();
        ItemDto item2 = createItemDto();
        item1.setDescription("test #1");
        item1.setAvailable(true);
        item2.setDescription("test #2");
        item2.setAvailable(true);
        itemService.addItem(owner.getId(), item1);
        itemService.addItem(owner.getId(), item2);
        List<ItemDto> searchResults = itemService.searchItems("test", 0, 10);
        assertEquals(2, searchResults.size());
        List<String> itemNames = searchResults.stream().map(ItemDto::getName).collect(Collectors.toList());
        assertTrue(itemNames.contains(item1.getName()));
        assertTrue(itemNames.contains(item2.getName()));
        //blank text
        searchResults = itemService.searchItems("", 0, 10);
        assertTrue(searchResults.isEmpty());
        //from <0 || size <= 0
        assertThrows(ObjectValidationException.class, () -> itemService.searchItems("test", 0, 0));
        assertThrows(ObjectValidationException.class, () -> itemService.searchItems("test", -1, 5));
    }

    @Test
    public void testAddCommentValidBooking() {
        User owner = createUser();
        User booker = createUser();
        ItemDto itemDto = createItemDto();
        ItemDto item = itemService.addItem(owner.getId(), itemDto);
        Booking booking = createBooking(booker, item.getId(), LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1));
        System.out.println(booking);
        bookingRepository.save(booking);
        CommentDto commentDto = createCommentDto(booker, ItemMapper.itemFromDto(item, owner));
        CommentDto addedComment = itemService.addComment(booker.getId(), item.getId(), commentDto);
        assertNotNull(addedComment.getId());
        assertEquals(commentDto.getText(), addedComment.getText());
        assertEquals(booker.getName(), addedComment.getAuthorName());
    }

    @Test
    public void testAddCommentInvalidBooking() {
        User owner = createUser();
        User booker = createUser();
        ItemDto item = itemService.addItem(owner.getId(), createItemDto());
        Booking booking = createBooking(booker, item.getId(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        bookingRepository.save(booking);
        CommentDto commentDto = createCommentDto(booker, ItemMapper.itemFromDto(item, owner));
        assertThrows(ObjectValidationException.class, () -> itemService.addComment(booker.getId(), item.getId(), commentDto));
    }

    private User createUser() {
        User user = new User();
        user.setName("John Test");
        user.setEmail("john.test@example.com");
        userRepository.save(user);
        return user;
    }

    private ItemDto createItemDto() {
        ItemDto itemDto = easyRandom.nextObject(ItemDto.class);
        itemDto.setId(null);
        itemDto.setRequestId(null);
        return itemDto;
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

    private CommentDto createCommentDto(User commenter, Item item) {
        Comment comment = commentRepository.save(CommentMapper.commentFromDto(easyRandom.nextObject(CommentDto.class), commenter, item));
        comment.setCreated(LocalDateTime.now());
        return CommentMapper.commentToDto(comment);
    }
}
