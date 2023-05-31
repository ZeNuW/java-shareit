package ru.practicum.shareit.booking.model;

import lombok.*;
import ru.practicum.shareit.item.model.Item;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(name = "booking_status")
    private BookingStatus status;
    @Column(name = "start_time")
    private LocalDateTime startOfBooking;
    @Column(name = "end_time")
    private LocalDateTime endOfBooking;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    @ToString.Exclude
    private Item item;
    @Column(name = "booker_id")
    private Long booker;
}