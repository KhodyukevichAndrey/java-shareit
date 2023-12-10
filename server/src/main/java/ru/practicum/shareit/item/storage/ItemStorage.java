package ru.practicum.shareit.item.storage;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.RequestItem;

import java.util.List;

public interface ItemStorage extends JpaRepository<Item, Long> {

    List<Item> findByOwnerIdOrderById(Long itemId, PageRequest pr);

    @Query("select i from Item i " +
            "where (lower(i.name) like lower(concat('%', ?1, '%')) " +
            "or lower(i.description) like lower(concat('%', ?1, '%'))) " +
            "and i.available = true")
    List<Item> searchItem(String text, PageRequest pr);

    List<Item> findByRequestItemInOrderById(List<RequestItem> requests);
}
