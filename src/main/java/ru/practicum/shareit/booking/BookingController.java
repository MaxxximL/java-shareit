package ru.practicum.shareit.booking;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDto create(@RequestBody BookingDto bookingDto,
                                     @RequestHeader("X-Sharer-User-Id") Long userId) {
        bookingDto.setBookerId(userId);
        return BookingMapper.toResponseDto(bookingService.create(userId, bookingDto));
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approve(@PathVariable Long bookingId,
                                      @RequestParam boolean approved,
                                      @RequestHeader("X-Sharer-User-Id") Long userId) throws AccessDeniedException {
        return BookingMapper.toResponseDto(bookingService.approve(userId, bookingId, approved));
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getById(@PathVariable Long bookingId,
                                      @RequestHeader("X-Sharer-User-Id") Long userId) {
        return BookingMapper.toResponseDto(bookingService.getById(userId, bookingId));
    }

    @GetMapping
    public List<BookingResponseDto> getAllByBooker(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "ALL") String state,
            HttpServletRequest request) {

        // Validate user exists first
        bookingService.validateUserExists(userId);

        BookingState bookingState = BookingState.from(state)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));

        return bookingService.getAllByBooker(userId, bookingState).stream()
                .map(BookingMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getAllByOwner(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "ALL") String state) {
        BookingState bookingState = BookingState.from(state)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));
        return bookingService.getAllByOwner(userId, bookingState).stream()
                .map(BookingMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}