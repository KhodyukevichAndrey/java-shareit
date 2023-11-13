package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.comment.Comment;

import java.util.List;

@Repository
public interface CommentStorage extends JpaRepository<Comment, Long> {

    @Query("select c " +
            "from Comment c " +
            "where c.item.id = :itemId " +
            "order by c.created desc")
    List<Comment> findCommentsByItemId(long itemId);

    @Query("select c " +
            "from Comment c " +
            "where c.item.owner.id = :ownerId " +
            "order by c.created desc")
    List<Comment> findCommentByOwnerId(long ownerId);
}
