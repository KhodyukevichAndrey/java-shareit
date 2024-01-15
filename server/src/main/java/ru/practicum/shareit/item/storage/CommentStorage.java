package ru.practicum.shareit.item.storage;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface CommentStorage extends JpaRepository<Comment, Long> {

    @Query("select c " +
            "from Comment c " +
            "where c.item.id = :itemId ")
    List<Comment> findCommentsByItemId(long itemId, Sort sort);

    List<Comment> findByItemIn(List<Item> items, Sort sort);
}
