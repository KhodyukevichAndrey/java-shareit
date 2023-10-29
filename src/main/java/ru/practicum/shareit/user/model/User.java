package ru.practicum.shareit.user.model;

import lombok.Data;

@Data
public class User {
    private long id;
    private final String name;
    private final String email;
}
