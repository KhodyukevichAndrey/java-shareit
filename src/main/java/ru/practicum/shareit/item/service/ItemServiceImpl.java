package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final ItemMapper itemMapper;
    private final UserStorage userStorage;
    private static final String WRONG_USER_ID = "Пользователь с указанным ID не найден";
    private static final String WRONG_ITEM_ID = "Предмет с указанным ID не найден";

    @Override
    public ItemDto addItem(long ownerId, ItemDto itemDto) {
        getUser(ownerId);
        Item item = itemMapper.makeItem(ownerId, itemDto);
        return itemMapper.makeItemDto(itemStorage.addItem(item));
    }

    @Override
    public ItemDto updateItem(long ownerId, long itemId, ItemDto itemDto) {
        Item oldItem = getItem(itemId);
        getUser(ownerId);
        itemDto.setId(itemId);

        if (oldItem.getOwnerId() == ownerId) {
            return itemMapper.makeItemDto(itemStorage.updateItem(updateItemFields(itemDto, oldItem)));
        } else {
            throw new EntityNotFoundException("Пользователь не может вносить изменения в предметы," +
                    " которые были добавлены другим пользователем");
        }
    }

    @Override
    public ItemDto getItemDto(long itemId) {
        return itemMapper.makeItemDto(getItem(itemId));
    }

    @Override
    public List<ItemDto> getAllOwnersItems(long ownerId) {
        getUser(ownerId);
        return itemStorage.getAllOwnersItems(ownerId).stream()
                .map(itemMapper::makeItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteItem(long ownerId, long itemId) {
        getUser(ownerId);
        itemStorage.deleteItem(ownerId, itemId);
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

    private Item updateItemFields(ItemDto itemDto, Item oldItem) {
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
        return oldItem;
    }

    private User getUser(long userId) {
        return userStorage.getUser(userId)
                .orElseThrow(() -> new EntityNotFoundException(WRONG_USER_ID));
    }

    private Item getItem(long itemId) {
        return itemStorage.getItem(itemId)
                .orElseThrow(() -> new EntityNotFoundException(WRONG_ITEM_ID));
    }
}
