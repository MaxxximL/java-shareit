package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    @Transactional
    public Item create(Long userId, ItemDto itemDto) {
        User owner = userService.getById(userId);
        ItemRequest request = null;

        if (itemDto.getRequestId() != null) {
            request = itemRequestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Request not found"));
        }

        Item item = ItemMapper.toModel(itemDto, owner, request);
        return itemRepository.save(item);
    }

    @Override
    @Transactional
    public Item update(Long userId, Long itemId, ItemDto itemDto) throws AccessDeniedException {
        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));

        if (!existingItem.getOwner().getId().equals(userId)) {
            throw new AccessDeniedException("Only owner can update item");
        }

        if (itemDto.getName() != null) {
            existingItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            existingItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            existingItem.setAvailable(itemDto.getAvailable());
        }

        return itemRepository.save(existingItem);
    }

    @Override
    public ItemWithBookingsDto getById(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));

        BookingShortDto lastBooking = null;
        BookingShortDto nextBooking = null;

        if (item.getOwner().getId().equals(userId)) {
            List<Booking> lastBookings = bookingRepository.findLastBooking(itemId, LocalDateTime.now());
            if (!lastBookings.isEmpty()) {
                lastBooking = BookingMapper.toShortDto(lastBookings.get(0));
            }

            List<Booking> nextBookings = bookingRepository.findNextBooking(itemId, LocalDateTime.now());
            if (!nextBookings.isEmpty()) {
                nextBooking = BookingMapper.toShortDto(nextBookings.get(0));
            }
        }

        List<CommentDto> comments = commentRepository.findByItemId(itemId).stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toList());

        return ItemMapper.toItemWithBookingsDto(item, lastBooking, nextBooking, comments);
    }

    @Override
    public List<ItemWithBookingsDto> getAllByOwner(Long userId) {
        userService.getById(userId);
        return itemRepository.findByOwnerId(userId).stream()
                .map(item -> {
                    List<Booking> lastBookings = bookingRepository.findLastBooking(item.getId(), LocalDateTime.now());
                    BookingShortDto lastBooking = null;
                    if (!lastBookings.isEmpty()) {
                        lastBooking = BookingMapper.toShortDto(lastBookings.get(0));
                    }

                    List<Booking> nextBookings = bookingRepository.findNextBooking(item.getId(), LocalDateTime.now());
                    BookingShortDto nextBooking = null;
                    if (!nextBookings.isEmpty()) {
                        nextBooking = BookingMapper.toShortDto(nextBookings.get(0));
                    }

                    List<CommentDto> comments = commentRepository.findByItemId(item.getId()).stream()
                            .map(CommentMapper::toDto)
                            .collect(Collectors.toList());
                    return ItemMapper.toItemWithBookingsDto(item, lastBooking, nextBooking, comments);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchAvailableItems(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.searchAvailableItems(text).stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        User author = userService.getById(userId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));

        validateUserBookedItem(userId, itemId);

        Comment comment = CommentMapper.toModel(commentDto, item, author);
        comment.setCreated(LocalDateTime.now());
        return CommentMapper.toDto(commentRepository.save(comment));
    }

    private void validateUserBookedItem(Long userId, Long itemId) {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = bookingRepository.findByItemIdAndBookerIdAndEndIsBeforeAndStatus(
                itemId, userId, now, BookingStatus.APPROVED);

        if (bookings.isEmpty()) {
            throw new ValidationException("User has not booked this item");
        }
    }
}