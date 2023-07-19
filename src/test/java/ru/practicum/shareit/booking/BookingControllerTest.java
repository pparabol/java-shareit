package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.service.BookingService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingControllerTest {
    private final ObjectMapper mapper;

    @MockBean
    private final BookingService bookingService;

    private final MockMvc mvc;

    private final static String HEADER = "X-Sharer-User-Id";

    private final BookingDtoRequest dtoRequest = new BookingDtoRequest(
            1L,
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(3)
    );

    private final BookingDto bookingDto = BookingDto.builder()
            .id(1L)
            .start(dtoRequest.getStart())
            .end(dtoRequest.getEnd())
            .status(Status.WAITING)
            .build();

    @Test
    void saveNewBooking() throws Exception {
        when(bookingService.saveBooking(anyLong(), any())).thenReturn(bookingDto);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(dtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDto)));
    }

    @Test
    void saveNewBookingWithoutHeader() throws Exception {
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(dtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void saveNewBookingWithIncorrectDate() throws Exception {
        BookingDtoRequest incorrectBooking = new BookingDtoRequest(
                1L,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now()
        );

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(incorrectBooking))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1))
                .andExpect(status().isBadRequest());
    }

    @Test
    void saveNewBookingWithIncorrectItemId() throws Exception {
        BookingDtoRequest incorrectBooking = new BookingDtoRequest(
                null,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(incorrectBooking))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1))
                .andExpect(status().isBadRequest());
    }

    @Test
    void approveBooking() throws Exception {
        BookingDto expectedDto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(3))
                .status(Status.APPROVED)
                .build();

        when(bookingService.approveBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(expectedDto);

        mvc.perform(patch("/bookings/1")
                        .header(HEADER, 1)
                        .param("approved", "true")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expectedDto)));
    }

    @Test
    void approveBookingWithoutApproved() throws Exception {
        mvc.perform(patch("/bookings/1")
                        .header(HEADER, 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getBooking() throws Exception {
        when(bookingService.getBooking(anyLong(), anyLong())).thenReturn(bookingDto);

        mvc.perform(get("/bookings/1")
                        .header(HEADER, 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDto)));
    }

    @Test
    void getBookings() throws Exception {
        List<BookingDto> bookingList = List.of(bookingDto);

        when(bookingService.getBookerBookings(anyLong(), anyString(), any()))
                .thenReturn(bookingList);

        mvc.perform(get("/bookings")
                        .header(HEADER, 1)
                        .param("from", "0")
                        .param("size", "5")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingList)));
    }

    @Test
    void getBookingsByOwner() throws Exception {
        List<BookingDto> bookingList = List.of(bookingDto);

        when(bookingService.getOwnerBookings(anyLong(), anyString(), any()))
                .thenReturn(bookingList);

        mvc.perform(get("/bookings/owner")
                        .header(HEADER, 1)
                        .param("from", "0")
                        .param("size", "5")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingList)));
    }
}
