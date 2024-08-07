package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@SuppressWarnings("checkstyle:Regexp")
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findBookingsByBookerIdAndEndBeforeOrderByStartDesc(long bookerId, LocalDateTime now);

    List<Booking> findBookingsByBookerIdAndStartAfterOrderByStartDesc(long bookerId, LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId AND :currentTime BETWEEN b.start AND b.end")
    List<Booking> findCurrentBookings(long bookerId, LocalDateTime currentTime);

    List<Booking> findBookingsByBookerIdAndStatusEqualsOrderByStartDesc(long bookerId, BookingStatus status);

    List<Booking> findAllByBookerIdOrderByStartDesc(long bookerId);

    List<Booking> findBookingsByItemOwnerIdAndEndBeforeOrderByStartDesc(long ownerId, LocalDateTime now);

    List<Booking> findBookingsByItemOwnerIdAndStartAfterOrderByStartDesc(long ownerId, LocalDateTime now);

    @Query("SELECT b FROM Booking b join Item i WHERE i.owner.id = :ownerId AND :currentTime BETWEEN b.start AND b.end")
    List<Booking> findCurrentBookingsByOwner(long ownerId, LocalDateTime currentTime);

    List<Booking> findBookingsByItemOwnerIdAndStatusOrderByStartDesc(long ownerId, BookingStatus status);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(long ownerId);

    List<Booking> findByItemIdInAndStatus(Set<Long> itemIds, BookingStatus status);

    List<Booking> findByItemIdAndStatus(long itemId, BookingStatus status);

    List<Booking> findByItemIdAndBookerIdAndEndBefore(long itemId, long bookerId, LocalDateTime now);
}
