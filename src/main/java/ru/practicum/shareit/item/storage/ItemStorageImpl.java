package ru.practicum.shareit.item.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ItemStorageImpl implements ItemStorage {

    private long itemId = 1;
    private final Map<Long, Item> items = new HashMap<>();
    private final Map<Long, List<Long>> userItems = new HashMap<>();

    @Override
    public Item addItem(Item item) {
        item.setItemId(itemId++);

        userItems.computeIfAbsent(item.getOwnerId(), k -> new ArrayList<>()).add(item.getItemId());
        items.put(item.getItemId(), item);
        log.debug("Item успешно создан");

        return item;
    }

    @Override
    public Item updateItem(Item item) {
        items.put(item.getItemId(), item);
        log.debug("Item с ID - {} успешно обновлен", item.getItemId());
        return item;
    }

    @Override
    public Optional<Item> getItem(long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public List<Item> getAllOwnersItems(long id) {
        return userItems.get(id).stream()
                .map(items::get)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteItem(long ownerId, long id) {
        userItems.get(ownerId).remove(id);
        items.remove(id);
        log.debug("Item с ID - {} успешно удален", id);
    }

    @Override
    public List<Item> searchItem(String text) {
        String textForSearch = text.toLowerCase();

        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(textForSearch) ||
                        item.getDescription().toLowerCase().contains(textForSearch))
                .collect(Collectors.toList());
    }
}
