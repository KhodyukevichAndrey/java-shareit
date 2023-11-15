package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentStorage;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.*;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final ItemMapper itemMapper;
    private final UserStorage userStorage;
    private final BookingStorage bookingStorage;
    private final BookingMapper bookingMapper;
    private final CommentStorage commentStorage;
    private final CommentMapper commentMapper;
    private static final String WRONG_USER_ID = "Пользователь с указанным ID не найден";
    private static final String WRONG_ITEM_ID = "Предмет с указанным ID не найден";
    private static final Sort SORT_BY_CREATED_DESC = Sort.by(Sort.Direction.DESC, "created");
    private static final Sort SORT_BY_START_DESC = Sort.by(Sort.Direction.DESC, "start");

    @Override
    @Transactional
    public ItemDto addItem(long ownerId, ItemDto itemDto) {
        User user = getUser(ownerId);
        Item item = itemMapper.makeItem(itemDto, user);
        return itemMapper.makeItemDto(itemStorage.save(item));
    }

    @Override
    @Transactional
    public ItemDto updateItem(long ownerId, long itemId, ItemDto itemDto) {
        Item oldItem = getItem(itemId);
        User user = getUser(ownerId);
        itemDto.setId(itemId);

        if (oldItem.getOwner().getId() == ownerId) {
            return itemMapper.makeItemDto(itemStorage.save(updateItemFields(itemDto, oldItem, user)));
        } else {
            throw new EntityNotFoundException("Пользователь не может вносить изменения в предметы," +
                    " которые были добавлены другим пользователем");
        }
    }

    @Override
    public ItemDto getItemDto(long userId, long itemId) {
        Item item = getItem(itemId);
        List<CommentResponseDto> commentsDto = commentStorage.findCommentsByItemId(itemId, SORT_BY_CREATED_DESC)
                .stream()
                .map(commentMapper::makeCommentResponseDto)
                .collect(toList());

        if (item.getOwner().getId() == userId) {
            List<Booking> allOwnerBookings = bookingStorage.findBookingsByItemIdAndStatusNot(itemId,
                    SORT_BY_START_DESC,
                    BookingStatus.REJECTED);

            BookingShortDto lastBooking = getLastBooking(allOwnerBookings);
            BookingShortDto nextBooking = getNextBooking(allOwnerBookings);

            return itemMapper.makeItemForOwnerDto(item, lastBooking, nextBooking, commentsDto);
        } else {
            return itemMapper.makeItemForOwnerDto(item, null, null, commentsDto);
        }
    }

    @Override
    public List<ItemResponseDto> getAllOwnersItems(long ownerId) {
        getUser(ownerId);
        List<Item> items = itemStorage.findByOwnerIdOrderById(ownerId);

        return makeListItemDto(items);
    }

    @Override
    @Transactional
    public void deleteItem(long ownerId, long itemId) {
        Item item = getItem(itemId);
        getUser(ownerId);
        if (item.getOwner().getId() == ownerId) {
            itemStorage.deleteById(itemId);
        } else {
            throw new EntityNotFoundException("Пользователь не может удалять предметы," +
                    " которые были добавлены другим пользователем");
        }
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }

        return itemStorage.searchItem(text).stream()
                .map(itemMapper::makeItemDto)
                .collect(toList());
    }

    @Override
    @Transactional
    public CommentResponseDto addComment(CommentRequestDto commentRequestDto, long userId, long itemId) {
        User author = getUser(userId);
        Item item = getItem(itemId);

        boolean isBookingExists = bookingStorage.existsByItemIdAndEndBeforeAndBookerIdIs(itemId,
                LocalDateTime.now(), userId);

        if (isBookingExists) {
            Comment comment = commentMapper.makeComment(commentRequestDto, item, author);
            return commentMapper.makeCommentResponseDto(commentStorage.save(comment));
        } else {
            throw new NotAvailableException("Комментарии к предметам могут оставлять пользователи," +
                    " которые брали предмет в аренду");
        }
    }

    private Item updateItemFields(ItemDto itemDto, Item oldItem, User user) {
        String name = itemDto.getName();
        String description = itemDto.getDescription();
        if (name != null && !name.isBlank()) {
            oldItem.setName(name);
        }
        if (description != null && !description.isBlank()) {
            oldItem.setDescription(description);
        }
        if (itemDto.getAvailable() != null) {
            oldItem.setAvailable(itemDto.getAvailable());
        }
        oldItem.setOwner(user);
        return oldItem;
    }

    private User getUser(long userId) {
        return userStorage.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(WRONG_USER_ID));
    }

    private Item getItem(long itemId) {
        return itemStorage.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException(WRONG_ITEM_ID));
    }

    private BookingShortDto getLastBooking(List<Booking> bookings) {
        if (!bookings.isEmpty()) {
            return bookings.stream()
                    .filter(booking -> !booking.getStart().isAfter(LocalDateTime.now()))
                    .findFirst()
                    .map(bookingMapper::makeBookingShortDto)
                    .orElse(null);
        } else {
            return null;
        }
    }

    private BookingShortDto getNextBooking(List<Booking> bookings) {
        if (!bookings.isEmpty()) {
            return bookings.stream()
                    .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                    .reduce((first, second) -> second)
                    .map(bookingMapper::makeBookingShortDto)
                    .orElse(null);
        } else {
            return null;
        }
    }

    private List<ItemResponseDto> makeListItemDto(List<Item> items) {
        Map<Item, List<Comment>> commentsByItem = commentStorage.findByItemIn(items, SORT_BY_CREATED_DESC)
                .stream()
                .collect(groupingBy(Comment::getItem, toList()));

        Map<Item, List<Booking>> bookingsByItem = bookingStorage.findByItemInAndStatusNot(items,
                        SORT_BY_START_DESC,
                        BookingStatus.REJECTED)
                .stream()
                .collect(groupingBy(Booking::getItem, toList()));

        List<ItemResponseDto> itemForOwnerDtoList = new LinkedList<>();

        for (Item item : items) {
            itemForOwnerDtoList.add(itemMapper.makeItemForOwnerDto(
                    item,
                    getLastBooking(bookingsByItem.getOrDefault(item, Collections.emptyList())),
                    getNextBooking(bookingsByItem.getOrDefault(item, Collections.emptyList())),
                    commentsByItem.getOrDefault(item, Collections.emptyList()).stream()
                            .map(commentMapper::makeCommentResponseDto)
                            .collect(toList())
            ));
        }
        return itemForOwnerDtoList;
    }
}
