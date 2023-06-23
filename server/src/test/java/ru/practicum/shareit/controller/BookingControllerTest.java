package ru.practicum.shareit.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
@ExtendWith({MockitoExtension.class, SpringExtension.class})
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookingService bookingService;
    private static BookingDto bookingDto1;
    private static BookingDto bookingDto2;
    private static final User owner = new User(1L, "test@test.com", "name");
    private static final ItemDto itemDto = new ItemDto(1L, "описание", "имя", true, 1L);
    private static final LocalDateTime nowTime = LocalDateTime.now();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @BeforeAll
    static void setUp() {
        Item item = ItemMapper.itemFromDto(itemDto, owner);
        Booking booking1 = new Booking(1L, BookingStatus.WAITING, nowTime.plusDays(1),
                nowTime.plusDays(2), item, 2L);
        Booking booking2 = new Booking(2L, BookingStatus.WAITING, nowTime.plusDays(3),
                nowTime.plusDays(4), item, 2L);
        bookingDto1 = BookingMapper.bookingToDto(booking1);
        bookingDto2 = BookingMapper.bookingToDto(booking2);
    }

    @Test
    public void createBookingTest() throws Exception {
        when(bookingService.createBooking(any(BookingDto.class), anyLong()))
                .thenReturn(bookingDto1);
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content("{\"itemId\":2,\"start\":\"" + nowTime.plusHours(1) + "\",\"end\":\"" + nowTime.plusHours(2) + "\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andExpect(jsonPath("$.start").value(nowTime.plusDays(1).format(formatter)))
                .andExpect(jsonPath("$.end").value(nowTime.plusDays(2).format(formatter)))
                .andExpect(jsonPath("$.item.id").value(1))
                .andExpect(jsonPath("$.item.name").value("имя"))
                .andExpect(jsonPath("$.booker.id").value(2));
    }

    @Test
    public void considerBookingTest() throws Exception {
        when(bookingService.considerBooking(anyBoolean(), anyLong(), anyLong()))
                .thenReturn(bookingDto1);
        mockMvc.perform(patch("/bookings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 12345L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andExpect(jsonPath("$.start").value(nowTime.plusDays(1).format(formatter)))
                .andExpect(jsonPath("$.end").value(nowTime.plusDays(2).format(formatter)))
                .andExpect(jsonPath("$.item.id").value(1))
                .andExpect(jsonPath("$.item.name").value("имя"))
                .andExpect(jsonPath("$.booker.id").value(2));
    }

    @Test
    public void getBookingTest() throws Exception {
        when(bookingService.getBooking(anyLong(), anyLong()))
                .thenReturn(bookingDto1);
        mockMvc.perform(get("/bookings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andExpect(jsonPath("$.start").value(nowTime.plusDays(1).format(formatter)))
                .andExpect(jsonPath("$.end").value(nowTime.plusDays(2).format(formatter)))
                .andExpect(jsonPath("$.item.id").value(1))
                .andExpect(jsonPath("$.item.name").value("имя"))
                .andExpect(jsonPath("$.booker.id").value(2));
    }

    @Test
    public void getUserBookingsTest() throws Exception {
        List<BookingDto> bookingList = Arrays.asList(bookingDto1, bookingDto2);
        when(bookingService.getUserBookings(anyString(), anyLong(), anyInt(), anyInt()))
                .thenReturn(bookingList);
        mockMvc.perform(get("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].status").value("WAITING"))
                .andExpect(jsonPath("$[0].start").value(nowTime.plusDays(1).format(formatter)))
                .andExpect(jsonPath("$[0].end").value(nowTime.plusDays(2).format(formatter)))
                .andExpect(jsonPath("$[0].item.id").value(1))
                .andExpect(jsonPath("$[0].item.name").value("имя"))
                .andExpect(jsonPath("$[0].booker.id").value(2))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].status").value("WAITING"))
                .andExpect(jsonPath("$[1].start").value(nowTime.plusDays(3).format(formatter)))
                .andExpect(jsonPath("$[1].end").value(nowTime.plusDays(4).format(formatter)))
                .andExpect(jsonPath("$[1].item.id").value(1))
                .andExpect(jsonPath("$[1].item.name").value("имя"))
                .andExpect(jsonPath("$[1].booker.id").value(2));
    }

    @Test
    public void getUserItemBookingsTest() throws Exception {
        List<BookingDto> bookingList = Arrays.asList(bookingDto1, bookingDto2);
        when(bookingService.getUserItemBookings(anyString(), anyLong(), anyInt(), anyInt()))
                .thenReturn(bookingList);
        mockMvc.perform(get("/bookings/owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].status").value("WAITING"))
                .andExpect(jsonPath("$[0].start").value(nowTime.plusDays(1).format(formatter)))
                .andExpect(jsonPath("$[0].end").value(nowTime.plusDays(2).format(formatter)))
                .andExpect(jsonPath("$[0].item.id").value(1))
                .andExpect(jsonPath("$[0].item.name").value("имя"))
                .andExpect(jsonPath("$[0].booker.id").value(2))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].status").value("WAITING"))
                .andExpect(jsonPath("$[1].start").value(nowTime.plusDays(3).format(formatter)))
                .andExpect(jsonPath("$[1].end").value(nowTime.plusDays(4).format(formatter)))
                .andExpect(jsonPath("$[1].item.id").value(1))
                .andExpect(jsonPath("$[1].item.name").value("имя"))
                .andExpect(jsonPath("$[1].booker.id").value(2));
    }
}