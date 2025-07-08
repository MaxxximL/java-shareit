package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto create(@Valid @RequestBody ItemDto itemDto,
                          @RequestHeader(USER_ID_HEADER) Long userId) {
        return ItemMapper.toDto(itemService.create(userId, itemDto));
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestBody ItemDto itemDto,
                          @PathVariable Long itemId,
                          @RequestHeader(USER_ID_HEADER) Long userId) throws AccessDeniedException {
        return ItemMapper.toDto(itemService.update(userId, itemId, itemDto));
    }

    @GetMapping("/{itemId}")
    public ItemWithBookingsDto getById(@PathVariable Long itemId,
                                       @RequestHeader(USER_ID_HEADER) Long userId) {
        return itemService.getById(userId, itemId);
    }

    @GetMapping
    public List<ItemWithBookingsDto> getAllByOwner(
            @RequestHeader(USER_ID_HEADER) Long userId) {
        return itemService.getAllByOwner(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchAvailableItems(
            @RequestParam String text) {
        return itemService.searchAvailableItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestBody CommentDto commentDto,
                                 @PathVariable Long itemId,
                                 @RequestHeader(USER_ID_HEADER) Long userId) {
        return itemService.addComment(userId, itemId, commentDto);
    }
}