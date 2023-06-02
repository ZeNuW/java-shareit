package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingShort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query(value = "UPDATE bookings b SET booking_status = :status " +
            "WHERE b.booking_id = :bookingId AND b.item_id IN " +
            "(SELECT item_id FROM items WHERE owner_id = :ownerId)", nativeQuery = true)
    @Transactional
    @Modifying
    void considerBooking(@Param("status") String status, @Param("bookingId") Long bookingId,
                         @Param("ownerId") Long ownerId);

    @Query("SELECT b FROM Booking b WHERE b.id = :bookingId AND (b.item.owner.id = :userId OR b.booker = :userId)")
    @Transactional(readOnly = true)
    Booking getBooking(@Param("bookingId") Long bookingId, @Param("userId") Long userId);

    @Query(value = "SELECT * FROM bookings WHERE " +
            "    ((:state = 'CURRENT' AND end_time >= :currentTimestamp AND start_time <= :currentTimestamp) OR\n" +
            "    (:state = 'PAST' AND end_time < :currentTimestamp AND booking_status = 'APPROVED') OR\n" +
            "    (:state = 'FUTURE' AND start_time > :currentTimestamp " +
            "AND (booking_status = 'APPROVED' OR booking_status = 'WAITING') ) OR\n" +
            "    (:state = 'WAITING' AND booking_status = 'WAITING') OR\n" +
            "    (:state = 'REJECTED' AND booking_status = 'REJECTED') OR\n" +
            "    :state = 'ALL') AND booker_id = :userId ORDER BY start_time DESC", nativeQuery = true)
    @Transactional(readOnly = true)
    List<Booking> getUserBookings(@Param("state") String state, @Param("userId") Long userId,
                                  @Param("currentTimestamp") LocalDateTime currentTimestamp);

    @Query(value = "SELECT b FROM Booking b WHERE " +
            "    ((:state = 'CURRENT' AND b.endOfBooking >= :currentTimestamp " +
            "AND b.startOfBooking <= :currentTimestamp) OR\n" +
            "    (:state = 'PAST' AND b.endOfBooking < :currentTimestamp AND b.status = 'APPROVED') OR\n" +
            "    (:state = 'FUTURE' AND b.startOfBooking > :currentTimestamp " +
            "AND (b.status = 'APPROVED' OR b.status = 'WAITING')) OR\n" +
            "    (:state = 'WAITING' AND b.status = 'WAITING') OR\n" +
            "    (:state = 'REJECTED' AND b.status = 'REJECTED') OR\n" +
            "    :state = 'ALL') AND b.item.owner.id = :userId ORDER BY b.startOfBooking DESC")
    @Transactional(readOnly = true)
    List<Booking> getUserItemBookings(@Param("state") String state, @Param("userId") Long userId,
                                      @Param("currentTimestamp") LocalDateTime currentTimestamp);

    @Query(value = "(SELECT b.booking_id as id, b.booker_id as booker, b.start_time as startofbooking, " +
            "b.end_time as endofbooking, b.item_id as item FROM bookings b " +
            "WHERE b.item_id IN (SELECT item_id FROM items i WHERE i.item_id = :itemId) " +
            "AND :currentTimestamp >= b.start_time AND b.booking_status = 'APPROVED' " +
            "ORDER BY b.end_time DESC LIMIT 1)" +
            "UNION ALL " +
            "(SELECT b.booking_id as id, b.booker_id as booker, b.start_time as startofbooking, " +
            "b.end_time as endofbooking, b.item_id as item FROM bookings b " +
            "WHERE b.item_id IN (SELECT item_id FROM items i WHERE i.item_id = :itemId) " +
            "AND :currentTimestamp <= b.start_time AND b.booking_status = 'APPROVED' " +
            "ORDER BY b.start_time LIMIT 1)", nativeQuery = true)
    @Transactional(readOnly = true)
    List<BookingShort> getNextAndLastItemBooking(@Param("itemId") Long itemId,
                                                 @Param("currentTimestamp") LocalDateTime currentTimestamp);

    @Transactional(readOnly = true)
    List<Booking> findAllByBookerAndItemIdAndStatusAndEndOfBookingIsBefore(
            Long userId, Long itemId, BookingStatus bookingStatus, LocalDateTime localDateTime);
}