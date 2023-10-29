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

        userItems.compute(item.getOwnerId(), (ownerId, itemsIds) -> {
            if (itemsIds == null) {
                itemsIds = new ArrayList<>();
            }
            itemsIds.add(item.getItemId());
            return itemsIds;
        });
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
        try {
            return Optional.of(items.get(id));
        } catch (NullPointerException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Item> getAllOwnersItems(long id) {
        return items.values().stream()
                .filter(item -> item.getOwnerId() == id)
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

        if (textForSearch.isBlank()) {
            return Collections.emptyList();
        }

        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(textForSearch) ||
                        item.getDescription().toLowerCase().contains(textForSearch))
                .collect(Collectors.toList());
    }
}
