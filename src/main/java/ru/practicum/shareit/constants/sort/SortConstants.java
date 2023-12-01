package ru.practicum.shareit.constants.sort;

import org.springframework.data.domain.Sort;

public class SortConstants {

    public static final Sort SORT_BY_CREATED_DESC = Sort.by(Sort.Direction.DESC, "created");
    public static final Sort SORT_BY_START_DESC = Sort.by(Sort.Direction.DESC, "start");
}
