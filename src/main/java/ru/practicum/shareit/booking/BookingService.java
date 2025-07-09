package ru.practicum.shareit.booking;

import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.List;

public interface BookingService {

    @Transactional
    Booking create(Long userId, BookingDto bookingDto);

    @Transactional
    Booking approve(Long userId, Long bookingId, boolean approved) throws AccessDeniedException;

    Booking getById(Long userId, Long bookingId);

    List<Booking> getAllByBooker(Long userId, BookingState state);

    List<Booking> getAllByOwner(Long userId, BookingState state);

    void validateUserExists(Long userId);

}