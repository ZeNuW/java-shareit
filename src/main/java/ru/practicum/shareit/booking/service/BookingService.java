package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {

    BookingDto createBooking(BookingDto bookingDto, Long userId);

    BookingDto considerBooking(Boolean approved, Long bookingId, Long userId);

    BookingDto getBooking(Long bookingId, Long userId);

    List<BookingDto> getUserBookings(String state, Long userId);

    List<BookingDto> getUserItemBookings(String state, Long userId);
}