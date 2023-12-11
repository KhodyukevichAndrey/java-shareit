package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.dto.RequestItemRequestDto;
import ru.practicum.shareit.request.dto.RequestItemResponseDto;
import ru.practicum.shareit.request.dto.RequestItemShortResponseDto;
import ru.practicum.shareit.request.mapper.RequestItemMapper;
import ru.practicum.shareit.request.model.RequestItem;
import ru.practicum.shareit.request.storage.RequestItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static ru.practicum.shareit.constants.error.ErrorConstants.WRONG_REQUEST_ID;
import static ru.practicum.shareit.constants.error.ErrorConstants.WRONG_USER_ID;
import static ru.practicum.shareit.constants.sort.SortConstants.SORT_BY_CREATED_DESC;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestItemServiceImpl implements RequestItemService {

    private final RequestItemStorage requestItemStorage;
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;

    @Override
    @Transactional
    public RequestItemShortResponseDto addRequest(long userId, RequestItemRequestDto requestDto) {
        getUser(userId);
        RequestItem requestItem = RequestItemMapper.makeRequestItem(requestDto, getUser(userId));
        return RequestItemMapper.makeShortResponse(requestItemStorage.save(requestItem));
    }

    @Override
    public List<RequestItemResponseDto> getMyRequests(long userId) {
        getUser(userId);
        List<RequestItem> requests = requestItemStorage.findByRequestorIdOrderByCreatedDesc(userId);
        return makeResponse(requests);
    }

    @Override
    public List<RequestItemResponseDto> getAllUserRequests(long userId, int from, int size) {
        getUser(userId);
        List<RequestItem> requestItems = requestItemStorage.findByRequestorIdNotOrderByCreatedDesc(userId,
                PageRequest.of(from / size, size, SORT_BY_CREATED_DESC)).getContent();
        return makeResponse(requestItems);
    }

    @Override
    public RequestItemResponseDto getRequestItemResponseDto(long userId, long requestId) {
        getUser(userId);
        RequestItem requestItem = getRequest(requestId);
        return makeResponse(List.of(requestItem)).get(0);
    }

    private RequestItem getRequest(long id) {
        return requestItemStorage.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(WRONG_REQUEST_ID));
    }

    private User getUser(long id) {
        return userStorage.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(WRONG_USER_ID));
    }

    private List<RequestItemResponseDto> makeResponse(List<RequestItem> requestItems) {
        Map<RequestItem, List<Item>> itemsByRequests = itemStorage.findByRequestItemInOrderById(requestItems)
                .stream()
                .collect(groupingBy(Item::getRequestItem, toList()));

        List<RequestItemResponseDto> response = new LinkedList<>();

        for (RequestItem requestItem : requestItems) {
            response.add(RequestItemMapper.makeMyResponseDto(
                    requestItem,
                    itemsByRequests.getOrDefault(requestItem, Collections.emptyList()).stream()
                            .map(ItemMapper::makeItemForRequestDto)
                            .collect(toList()))
            );
        }
        return response;
    }
}
