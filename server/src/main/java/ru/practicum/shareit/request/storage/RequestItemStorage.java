package ru.practicum.shareit.request.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.RequestItem;

import java.util.List;

public interface RequestItemStorage extends JpaRepository<RequestItem, Long> {

    List<RequestItem> findByRequestorIdOrderByCreatedDesc(long requestorId);

    Page<RequestItem> findByRequestorIdNotOrderByCreatedDesc(long userId, Pageable page);
}
