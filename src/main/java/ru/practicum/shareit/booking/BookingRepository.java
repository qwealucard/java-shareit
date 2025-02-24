package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId, Status status);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId ORDER BY b.start DESC")
    List<Booking> findByItemOwnerIdOrderByStartDesc(@Param("ownerId") Long ownerId);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId AND b.status = :status ORDER BY b.start DESC")
    List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(@Param("ownerId") Long ownerId, @Param("status") Status status);

    Optional<Booking> findByIdAndBookerId(Long bookingId, Long bookerId);

    Optional<Booking> findByIdAndItemOwnerId(Long bookingId, Long ownerId);

    List<Booking> findByItemIdOrderByStartDesc(Long itemId);

    List<Booking> findByItemIdAndBookerIdAndStatusAndEndBefore(Long itemId, Long bookerId, Status status, LocalDateTime endBefore);
}
