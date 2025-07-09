package ru.practicum.shareit.item;

import java.nio.file.AccessDeniedException;
import java.util.List;

public interface ItemService {

    Item create(Long userId, ItemDto itemDto);

    Item update(Long userId, Long itemId, ItemDto itemDto) throws AccessDeniedException;

    ItemWithBookingsDto getById(Long userId, Long itemId);

    List<ItemWithBookingsDto> getAllByOwner(Long userId);

    List<ItemDto> searchAvailableItems(String text);

    CommentDto addComment(Long userId, Long itemId, CommentDto commentDto);
}