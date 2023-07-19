package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId, Pageable pageable);

    @Query("from Booking as b " +
            "where b.booker.id = :bookerId " +
            "and b.start <= now() and b.end >= now() " +
            "order by b.start desc")
    List<Booking> findCurrentBookings(Long bookerId, Pageable pageable);

    @Query("from Booking as b " +
            "where b.booker.id = :bookerId " +
            "and b.start < now() and b.end <= now() " +
            "order by b.start desc")
    List<Booking> findPastBookings(Long bookerId, Pageable pageable);

    @Query("from Booking as b " +
            "where b.booker.id = :bookerId " +
            "and b.start > now() and b.end > now() " +
            "order by b.start desc")
    List<Booking> findFutureBookings(Long bookerId, Pageable pageable);

    List<Booking> findByBookerIdAndStatus(Long bookerId, Status status, Pageable pageable);

    List<Booking> findByItemOwnerIdOrderByStartDesc(Long ownerId, Pageable pageable);

    @Query("from Booking as b " +
            "where b.item.owner.id = :ownerId " +
            "and b.start <= now() and b.end >= now() " +
            "order by b.start desc")
    List<Booking> findOwnerCurrentBookings(Long ownerId, Pageable pageable);

    @Query("from Booking as b " +
            "where b.item.owner.id = :ownerId " +
            "and b.start < now() and b.end <= now() " +
            "order by b.start desc")
    List<Booking> findOwnerPastBookings(Long ownerId, Pageable pageable);

    @Query("from Booking as b " +
            "where b.item.owner.id = :ownerId " +
            "and b.start > now() and b.end > now() " +
            "order by b.start desc")
    List<Booking> findOwnerFutureBookings(Long ownerId, Pageable pageable);

    List<Booking> findByItemOwnerIdAndStatus(Long ownerId, Status status, Pageable pageable);

    Booking findFirstByItemIdAndStartBeforeOrderByStartDesc(Long itemId, LocalDateTime start);

    Booking findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(Long itemId, LocalDateTime start, Status status);

    Optional<Booking> findFirstByItemIdAndBookerIdAndEndBefore(Long itemId, Long bookerId, LocalDateTime end);
}
