package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoJsonTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void testSerialize() throws Exception {

        var dto = new ItemRequestDto();
        dto.setId(1L);
        dto.setName("Test Item");
        dto.setDescription("Test Description");
        dto.setAvailable(true);
        dto.setOwner(2L);
        dto.setRequestId(3L);

        var lastBooking = new BookingRequestDto();
        lastBooking.setItemId(4L);
        dto.setLastBooking(lastBooking);

        var nextBooking = new BookingRequestDto();
        nextBooking.setItemId(5L);
        dto.setNextBooking(nextBooking);

        var result = json.write(dto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.available");
        assertThat(result).hasJsonPath("$.owner");
        assertThat(result).hasJsonPath("$.requestId");
        assertThat(result).hasJsonPath("$.lastBooking");
        assertThat(result).hasJsonPath("$.nextBooking");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Test Item");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Test Description");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.owner").isEqualTo(2);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(3);
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.itemId").isEqualTo(4);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.itemId").isEqualTo(5);
    }
}
