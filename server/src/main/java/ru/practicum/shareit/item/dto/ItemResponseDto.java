package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingShortDto;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemResponseDto extends ItemDto {
    private BookingShortDto lastBooking;
    private BookingShortDto nextBooking;
    private List<CommentResponseDto> comments;

    public ItemResponseDto(ItemDto itemDto, BookingShortDto lastBooking, BookingShortDto nextBooking,
                           List<CommentResponseDto> comments) {
        super(itemDto.getId(), itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable(),
                itemDto.getRequestId());
        this.lastBooking = lastBooking;
        this.nextBooking = nextBooking;
        this.comments = comments;
    }
}
