package ru.practicum.shareit.booking;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

public class BookingMapper {

    public static Booking toModel(BookingDto bookingDto, Item item, User booker) {
        return new Booking(
                bookingDto.getId(),
                bookingDto.getStart(),
                bookingDto.getEnd(),
                item,
                booker,
                bookingDto.getStatus() != null ? bookingDto.getStatus() : BookingStatus.WAITING
        );
    }

    public static BookingResponseDto toResponseDto(Booking booking) {
        BookingResponseDto dto = new BookingResponseDto();
        dto.setId(booking.getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setStatus(booking.getStatus());

        // Item
        BookingResponseDto.ItemForBookingDto itemDto = new BookingResponseDto.ItemForBookingDto();
        itemDto.setId(booking.getItem().getId());
        itemDto.setName(booking.getItem().getName());
        dto.setItem(itemDto);

        // Booker
        BookingResponseDto.BookerDto bookerDto = new BookingResponseDto.BookerDto();
        bookerDto.setId(booking.getBooker().getId());
        dto.setBooker(bookerDto);

        return dto;
    }


    public static BookingShortDto toShortDto(Booking booking) {
        return new BookingShortDto(
                booking.getId(),
                booking.getBooker().getId(),
                booking.getStart(),
                booking.getEnd()
        );
    }

}