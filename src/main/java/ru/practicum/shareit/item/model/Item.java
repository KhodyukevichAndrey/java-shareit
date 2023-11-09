package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Item {
    private long ownerId;
    private long itemId;
    private String name;
    private String description;
    private Boolean available;
}
