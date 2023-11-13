package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
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
import java.util.stream.Collectors;

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
        List<CommentResponseDto> commentsDto = commentStorage.findCommentsByItemId(itemId).stream()
                .map(commentMapper::makeCommentResponseDto)
                .collect(Collectors.toList());

        if (item.getOwner().getId() == userId) {
            List<Booking> allOwnerBookings = bookingStorage.findAllBookingsByItemId(itemId);
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
        List<Booking> allOwnerBookings = bookingStorage.findAllBookingsByItemIdIn(
                items.stream()
                        .map(Item::getId)
                        .collect(Collectors.toList()));
        List<CommentResponseDto> commentDto = commentStorage.findCommentByOwnerId(ownerId).stream()
                .map(commentMapper::makeCommentResponseDto)
                .collect(Collectors.toList());


        return makeListItemDto(items, allOwnerBookings, commentDto);
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
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentResponseDto addComment(CommentRequestDto commentRequestDto, long userId, long itemId) {
        User author = getUser(userId);
        Item item = getItem(itemId);

        List<Long> itemBookings = bookingStorage.findAllBookingsByItemId(itemId).stream()
                .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                .map(booking -> booking.getBooker().getId())
                .filter(bookerId -> bookerId == userId)
                .collect(Collectors.toList());

        if (!itemBookings.isEmpty()) {
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
                    .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()))
                    .filter(booking -> booking.getStatus() != BookingStatus.REJECTED)
                    .max(Comparator.comparing(Booking::getEnd))
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
                    .filter(booking -> booking.getStatus() != BookingStatus.REJECTED)
                    .min(Comparator.comparing(Booking::getEnd))
                    .map(bookingMapper::makeBookingShortDto)
                    .orElse(null);
        } else {
            return null;
        }
    }

    private List<ItemResponseDto> makeListItemDto(List<Item> items, List<Booking> bookings,
                                                  List<CommentResponseDto> commentsDto) {
        List<ItemResponseDto> itemForOwnerDtoList = new ArrayList<>();
        for (Item item : items) {
            List<Booking> itemBookings = bookings.stream()
                    .filter(booking -> booking.getItem().getId() == item.getId())
                    .collect(Collectors.toList());

            itemForOwnerDtoList.add(itemMapper.makeItemForOwnerDto(item,
                    getLastBooking(itemBookings),
                    getNextBooking(itemBookings),
                    commentsDto));
        }
        return itemForOwnerDtoList;
    }
}
