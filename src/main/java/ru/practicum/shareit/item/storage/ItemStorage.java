package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {

    Item addItem(Item item);

    Item updateItem(Item item);

    Optional<Item> getItem(long id);

    List<Item> getAllOwnersItems(long id);

    void deleteItem(long ownerId, long id);

    List<Item> searchItem(String text);
}
