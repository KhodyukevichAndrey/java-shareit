package ru.practicum.shareit.booking.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingStorage extends JpaRepository<Booking, Long> {

    List<Booking> findBookingByBookerId(long bookerId, Pageable p);

    List<Booking> findBookingByItemOwnerId(long ownerId, Pageable p);

    List<Booking> findBookingsByItemIdAndStatusNot(long itemId, Sort sort, BookingStatus status);

    List<Booking> findAllBookingByBookerIdAndEndBefore(long bookerIdm, LocalDateTime now, Pageable p);

    List<Booking> findAllBookingByBookerIdAndStartBeforeAndEndAfter(long bookerId, LocalDateTime now,
                                                                    LocalDateTime sameNow, Pageable p);

    List<Booking> findAllBookingByBookerIdAndStartAfter(long bookerId, LocalDateTime now, Pageable p);

    List<Booking> findAllBookingByBookerIdAndStatusIs(long bookerId, BookingStatus status, Pageable p);

    List<Booking> findAllBookingByItemOwnerIdAndEndBefore(long bookerIdm, LocalDateTime now, Pageable p);

    List<Booking> findAllBookingByItemOwnerIdAndStartBeforeAndEndAfter(long bookerId, LocalDateTime now,
                                                                       LocalDateTime sameNow, Pageable p);

    List<Booking> findAllBookingByItemOwnerIdAndStartAfter(long bookerId, LocalDateTime now, Pageable p);

    List<Booking> findAllBookingByItemOwnerIdAndStatusIs(long bookerId, BookingStatus status, Pageable p);

    List<Booking> findByItemInAndStatusNot(List<Item> items, Sort sort, BookingStatus status);

    boolean existsByItemIdAndEndBeforeAndBookerIdIs(long itemId, LocalDateTime now, long bookerId);
}
