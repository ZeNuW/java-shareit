package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.exception.ObjectValidationException;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createBooking(BookingDto bookingDto, Long userId) {
        LocalDateTime startOfBooking = bookingDto.getStartOfBooking();
        LocalDateTime endOfBooking = bookingDto.getEndOfBooking();
        if (endOfBooking.isBefore(startOfBooking)) {
            throw new ObjectValidationException("Время окончания брони установлено раньше начала брони.");
        }
        if (startOfBooking.isEqual(endOfBooking)) {
            throw new ObjectValidationException("Время начала и конца брони установлено в одно время.");
        }
        return post("", userId, bookingDto);
    }

    public ResponseEntity<Object> considerBooking(Boolean approved, Long bookingId, long userId) {
        return patch("/" + bookingId + "?approved=" + approved, userId);
    }

    public ResponseEntity<Object> getBooking(Long bookingId, long userId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> getUserBookings(BookingState state, Long userId, int from, int size) {
        checkParameters(from, size);
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
        return get("?state={state}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getUserItemBookings(BookingState state, Long userId, int from, int size) {
        checkParameters(from, size);
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
        return get("/owner/?state={state}&from={from}&size={size}", userId, parameters);
    }

    private void checkParameters(int from, int size) {
        if (size <= 0) {
            throw new ObjectValidationException("size не может быть отрицательным или равным 0");
        }
        if (from < 0) {
            throw new ObjectValidationException("from не может быть отрицательным");
        }
    }
}