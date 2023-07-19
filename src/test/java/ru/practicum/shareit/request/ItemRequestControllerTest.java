package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestControllerTest {
    private final ObjectMapper mapper;

    @MockBean
    private final ItemRequestService itemRequestService;

    private final MockMvc mvc;

    private final static String HEADER = "X-Sharer-User-Id";

    private final ItemRequestDto itemRequestDto = ItemRequestDto.builder()
            .id(1L)
            .description("description")
            .created(LocalDateTime.now())
            .build();

    @Test
    void saveNewRequest() throws Exception {
        when(itemRequestService.saveItemRequest(anyLong(), any())).thenReturn(itemRequestDto);

        mvc.perform(post("/requests")
                        .header(HEADER, 1)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id", is(itemRequestDto.getId()), Long.class),
                        jsonPath("$.description", is(itemRequestDto.getDescription())),
                        jsonPath("$.created",
                                is(itemRequestDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                );
    }

    @Test
    void saveNewRequestWithoutHeader() throws Exception {
        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void saveNewRequestWithoutDescription() throws Exception {
        ItemRequestDto incorrectRequest = ItemRequestDto.builder().build();

        mvc.perform(post("/requests")
                        .header(HEADER, 1)
                        .content(mapper.writeValueAsString(incorrectRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllByRequestor() throws Exception {
        List<ItemRequestDto> requestList = List.of(itemRequestDto);

        when(itemRequestService.getAllByRequestor(anyLong())).thenReturn(requestList);

        mvc.perform(get("/requests")
                        .header(HEADER, 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(requestList)));
    }

    @Test
    void getAll() throws Exception {
        List<ItemRequestDto> requestList = List.of(itemRequestDto);

        when(itemRequestService.getAll(anyLong(), any())).thenReturn(requestList);

        mvc.perform(get("/requests/all")
                        .header(HEADER, 1)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("from", "0")
                        .param("size", "4"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(requestList)));
    }

    @Test
    void getItemRequest() throws Exception {
        when(itemRequestService.getItemRequest(anyLong(), anyLong())).thenReturn(itemRequestDto);

        mvc.perform(get("/requests/1")
                        .header(HEADER, 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id", is(itemRequestDto.getId()), Long.class),
                        jsonPath("$.description", is(itemRequestDto.getDescription())),
                        jsonPath("$.created",
                                is(itemRequestDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                );
    }
}
