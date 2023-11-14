package ru.practicum.shareit.booking.storage;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingStorage extends JpaRepository<Booking, Long> {

    List<Booking> findBookingByBookerId(long bookerId, Sort sort);

    List<Booking> findBookingByItemOwnerId(long ownerId, Sort sort);

    List<Booking> findBookingsByItemIdAndStatusNot(long itemId, Sort sort, BookingStatus status);

    List<Booking> findAllBookingByBookerIdAndEndBefore(long bookerIdm, LocalDateTime now, Sort sort);

    List<Booking> findAllBookingByBookerIdAndStartBeforeAndEndAfter(long bookerId, LocalDateTime now,
                                                                    LocalDateTime sameNow, Sort sort);

    List<Booking> findAllBookingByBookerIdAndStartAfter(long bookerId, LocalDateTime now, Sort sort);

    List<Booking> findAllBookingByBookerIdAndStatusIs(long bookerId, BookingStatus status, Sort sort);

    List<Booking> findAllBookingByItemOwnerIdAndEndBefore(long bookerIdm, LocalDateTime now, Sort sort);

    List<Booking> findAllBookingByItemOwnerIdAndStartBeforeAndEndAfter(long bookerId, LocalDateTime now,
                                                                       LocalDateTime sameNow, Sort sort);

    List<Booking> findAllBookingByItemOwnerIdAndStartAfter(long bookerId, LocalDateTime now, Sort sort);

    List<Booking> findAllBookingByItemOwnerIdAndStatusIs(long bookerId, BookingStatus status, Sort sort);

    List<Booking> findByItemInAndStatusNot(List<Item> items, Sort sort, BookingStatus status);

    boolean existsByItemIdAndEndBeforeAndBookerIdIs(long itemId, LocalDateTime now, long bookerId);
}
