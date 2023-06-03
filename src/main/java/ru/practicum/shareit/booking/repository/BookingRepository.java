package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.dto.BookingShort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

//    @Query(value = "UPDATE bookings b SET booking_status = :status " +
//            "WHERE b.booking_id = :bookingId AND b.item_id IN " +
//            "(SELECT item_id FROM items WHERE owner_id = :ownerId)", nativeQuery = true)
//    @Modifying
//    void considerBooking(@Param("status") String status, @Param("bookingId") Long bookingId,
//                         @Param("ownerId") Long ownerId);

    @Query("SELECT b FROM Booking b WHERE b.id = :bookingId AND (b.item.owner.id = :userId OR b.booker = :userId)")
    Booking getBooking(@Param("bookingId") Long bookingId, @Param("userId") Long userId);

    @Query(value = "SELECT * FROM bookings WHERE " +
            "    ((:state = 'CURRENT' AND end_time >= :currentTimestamp AND start_time <= :currentTimestamp) OR\n" +
            "    (:state = 'PAST' AND end_time < :currentTimestamp AND booking_status = 'APPROVED') OR\n" +
            "    (:state = 'FUTURE' AND start_time > :currentTimestamp " +
            "AND (booking_status = 'APPROVED' OR booking_status = 'WAITING') ) OR\n" +
            "    (:state = 'WAITING' AND booking_status = 'WAITING') OR\n" +
            "    (:state = 'REJECTED' AND booking_status = 'REJECTED') OR\n" +
            "    :state = 'ALL') AND booker_id = :userId ORDER BY start_time DESC", nativeQuery = true)
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
    List<Booking> getUserItemBookings(@Param("state") String state, @Param("userId") Long userId,
                                      @Param("currentTimestamp") LocalDateTime currentTimestamp);

    @Query(value = "SELECT b.booking_id AS id, b.booker_id AS booker, b.start_time AS startofbooking," +
            "b.end_time AS endofbooking, b.item_id AS item " +
            "FROM bookings b " +
            "WHERE b.item_id IN (:itemId) " +
            "AND b.booking_status = 'APPROVED' " +
            "AND ((b.start_time = (SELECT MAX(start_time) FROM bookings WHERE item_id IN (:itemId) " +
            "AND :currentTimestamp >= start_time AND booking_status = 'APPROVED' AND b.item_id = item_id)) " +
            "OR " +
            "(b.start_time = (SELECT MIN(start_time) FROM bookings WHERE item_id IN (:itemId) " +
            "AND :currentTimestamp <= start_time AND booking_status = 'APPROVED' AND b.item_id = item_id)))",
            nativeQuery = true)
    List<BookingShort> getNextAndLastItemBooking(@Param("itemId") List<Long> itemId,
                                                 @Param("currentTimestamp") LocalDateTime currentTimestamp);

    List<Booking> findAllByBookerAndItemIdAndStatusAndEndOfBookingIsBefore(
            Long userId, Long itemId, BookingStatus bookingStatus, LocalDateTime localDateTime);
}