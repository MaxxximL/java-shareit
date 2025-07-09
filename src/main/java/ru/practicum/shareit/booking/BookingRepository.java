package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdAndEndIsBefore(Long bookerId, LocalDateTime end, Sort sort);

    List<Booking> findByBookerIdAndStartIsAfter(Long bookerId, LocalDateTime start, Sort sort);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfter(Long bookerId, LocalDateTime start, LocalDateTime end, Sort sort);

    List<Booking> findByBookerIdAndStatus(Long bookerId, BookingStatus status, Sort sort);

    List<Booking> findByBookerId(Long bookerId, Sort sort);

    List<Booking> findByItemOwnerIdAndEndIsBefore(Long ownerId, LocalDateTime end, Sort sort);

    List<Booking> findByItemOwnerIdAndStartIsAfter(Long ownerId, LocalDateTime start, Sort sort);

    List<Booking> findByItemOwnerIdAndStartBeforeAndEndAfter(Long ownerId, LocalDateTime start, LocalDateTime end, Sort sort);

    List<Booking> findByItemOwnerIdAndStatus(Long ownerId, BookingStatus status, Sort sort);

    List<Booking> findByItemOwnerId(Long ownerId, Sort sort);

    @Query("select b from Booking b " +
            "where b.item.id = ?1 " +
            "and b.status = 'APPROVED' " +
            "and b.end < ?2 " +
            "order by b.end desc")
    List<Booking> findLastBooking(Long itemId, LocalDateTime now);

    @Query("select b from Booking b " +
            "where b.item.id = ?1 " +
            "and b.status = 'APPROVED' " +
            "and b.start > ?2 " +
            "order by b.start asc")
    List<Booking> findNextBooking(Long itemId, LocalDateTime now);

    List<Booking> findByItemIdAndBookerIdAndEndIsBeforeAndStatus(Long itemId, Long bookerId, LocalDateTime end, BookingStatus status);

    @EntityGraph(attributePaths = {"item", "booker"})
    Optional<Booking> findById(Long id);
}