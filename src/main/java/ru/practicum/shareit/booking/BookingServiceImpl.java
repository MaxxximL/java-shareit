package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;
    private final ItemRepository itemRepository;

    @Transactional
    @Override
    public Booking create(Long userId, BookingDto bookingDto) {
        User booker = userService.getById(userId);
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found"));

        if (!item.getAvailable()) {
            throw new ValidationException("Item is not available for booking");
        }

        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("User cannot book his own item");
        }

        validateBookingDates(bookingDto.getStart(), bookingDto.getEnd());

        Booking booking = BookingMapper.toModel(bookingDto, item, booker);
        booking.setStatus(BookingStatus.WAITING);
        return bookingRepository.save(booking);
    }

    @Transactional
    @Override
    public Booking approve(Long userId, Long bookingId, boolean approved) throws AccessDeniedException {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new AccessDeniedException("Only item owner can approve booking");
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Booking is already approved or rejected");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking getById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        if (!booking.getBooker().getId().equals(userId) &&
                !booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Only booker or owner can view booking");
        }

        return booking;
    }

    public void validateUserExists(Long userId) {
        userService.getById(userId);
    }

    @Override
    public List<Booking> getAllByBooker(Long userId, BookingState state) {
        userService.getById(userId); // validate user exists
        LocalDateTime now = LocalDateTime.now();
        Sort sort = Sort.by(Sort.Direction.DESC, "start");

        switch (state) {
            case PAST:
                return bookingRepository.findByBookerIdAndEndIsBefore(userId, now, sort);
            case FUTURE:
                return bookingRepository.findByBookerIdAndStartIsAfter(userId, now, sort);
            case CURRENT:
                return bookingRepository.findByBookerIdAndStartBeforeAndEndAfter(userId, now, now, sort);
            case WAITING:
                return bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.WAITING, sort);
            case REJECTED:
                return bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.REJECTED, sort);
            default:
                return bookingRepository.findByBookerId(userId, sort);
        }
    }

    @Override
    public List<Booking> getAllByOwner(Long userId, BookingState state) {
        userService.getById(userId); // validate user exists
        LocalDateTime now = LocalDateTime.now();
        Sort sort = Sort.by(Sort.Direction.DESC, "start");

        switch (state) {
            case PAST:
                return bookingRepository.findByItemOwnerIdAndEndIsBefore(userId, now, sort);
            case FUTURE:
                return bookingRepository.findByItemOwnerIdAndStartIsAfter(userId, now, sort);
            case CURRENT:
                return bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfter(userId, now, now, sort);
            case WAITING:
                return bookingRepository.findByItemOwnerIdAndStatus(userId, BookingStatus.WAITING, sort);
            case REJECTED:
                return bookingRepository.findByItemOwnerIdAndStatus(userId, BookingStatus.REJECTED, sort);
            default:
                return bookingRepository.findByItemOwnerId(userId, sort);
        }
    }

    private void validateBookingDates(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            throw new ValidationException("Start and end dates must be specified");
        }

        if (start.isAfter(end)) {
            throw new ValidationException("Start date must be before end date");
        }

        if (end.isBefore(LocalDateTime.now())) {
            throw new ValidationException("End date cannot be in the past");
        }

        if (start.isEqual(end)) {
            throw new ValidationException("Start and end dates cannot be equal");
        }

    }
}