package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto createBooking(@RequestBody BookingDto bookingDto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.createBooking(bookingDto, userId);
    }

    @PatchMapping("{bookingId}")
    public BookingDto considerBooking(@RequestParam Boolean approved, @PathVariable Long bookingId,
                                      @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.considerBooking(approved, bookingId, userId);
    }

    @GetMapping("{bookingId}")
    public BookingDto getBooking(@PathVariable Long bookingId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getUserBookings(@RequestParam(defaultValue = "ALL") String state,
                                            @RequestHeader("X-Sharer-User-Id") Long userId,
                                            @RequestParam(defaultValue = "0") int from,
                                            @RequestParam(defaultValue = "10") int size) {
        return bookingService.getUserBookings(state, userId, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getUserItemBookings(@RequestParam(defaultValue = "ALL") String state,
                                                @RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestParam(defaultValue = "0") int from,
                                                @RequestParam(defaultValue = "10") int size) {
        return bookingService.getUserItemBookings(state, userId, from, size);
    }
}