package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoRequestJsonTest {

    @Autowired
    private JacksonTester<BookingDtoRequest> json;

    @Test
    void testBookingDtoRequest() throws Exception {
        BookingDtoRequest bookingDtoRequest = new BookingDtoRequest(
                1L,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(3)
        );

        JsonContent<BookingDtoRequest> result = json.write(bookingDtoRequest);

        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo(bookingDtoRequest.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(result).extractingJsonPathStringValue("$.end").
                isEqualTo(bookingDtoRequest.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }
}
