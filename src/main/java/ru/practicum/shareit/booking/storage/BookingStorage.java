package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

@Repository
public interface BookingStorage extends JpaRepository<Booking, Long> {

    @Query("select b " +
            "from Booking b " +
            "where b.booker.id = :bookerId " +
            "order by b.start desc")
    List<Booking> findBookingByBookerId(long bookerId);

    @Query("select b " +
            "from Booking b " +
            "where b.item.owner.id = :ownerId " +
            "order by b.start desc")
    List<Booking> getBookingByOwnerId(long ownerId);

    @Query("select b " +
            "from Booking b " +
            "where b.item.id = :itemId " +
            "order by b.start desc")
    List<Booking> findAllBookingsByItemId(long itemId);

    List<Booking> findAllBookingsByItemIdIn(List<Long> ids);
}
