package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

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

@WebMvcTest(ItemController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemControllerTest {
    private final ObjectMapper mapper;

    @MockBean
    private final ItemService itemService;

    private final MockMvc mvc;

    private final static String HEADER = "X-Sharer-User-Id";

    private final ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("Item")
            .description("description")
            .available(true)
            .build();

    private final CommentDto commentDto = new CommentDto(
            1L,
            "comment",
            "author",
            LocalDateTime.now()
    );

    @Test
    void getItems() throws Exception {
        List<ItemDto> itemList = List.of(itemDto);

        when(itemService.getItems(anyLong(), any())).thenReturn(itemList);

        mvc.perform(get("/items")
                        .header(HEADER, 1)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("from", "0")
                        .param("size", "3"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemList)));
    }

    @Test
    void getItem() throws Exception {
        when(itemService.getItem(anyLong(), anyLong())).thenReturn(itemDto);

        mvc.perform(get("/items/1")
                        .header(HEADER, 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemDto)));
    }

    @Test
    void saveNewItem() throws Exception {
        when(itemService.saveItem(anyLong(), any())).thenReturn(itemDto);

        mvc.perform(post("/items")
                        .header(HEADER, 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id", is(itemDto.getId()), Long.class),
                        jsonPath("$.name", is(itemDto.getName())),
                        jsonPath("$.description", is(itemDto.getDescription())),
                        jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class)
                );
    }

    @Test
    void saveNewItemWithoutHeader() throws Exception {
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void saveNewItemWithIncorrectName() throws Exception {
        ItemDto incorrectItem = ItemDto.builder()
                .name(" ")
                .description("normal")
                .available(true)
                .build();

        mvc.perform(post("/items")
                        .header(HEADER, 1)
                        .content(mapper.writeValueAsString(incorrectItem))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void saveNewItemWithIncorrectDescription() throws Exception {
        ItemDto incorrectItem = ItemDto.builder()
                .name("normal")
                .description("")
                .available(true)
                .build();

        mvc.perform(post("/items")
                        .header(HEADER, 1)
                        .content(mapper.writeValueAsString(incorrectItem))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void saveNewItemWithoutAvailable() throws Exception {
        ItemDto incorrectItem = ItemDto.builder()
                .name("normal")
                .description("normal")
                .build();

        mvc.perform(post("/items")
                        .header(HEADER, 1)
                        .content(mapper.writeValueAsString(incorrectItem))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateItem() throws Exception {
        when(itemService.updateItem(anyLong(), any())).thenReturn(itemDto);

        mvc.perform(patch("/items/1")
                        .header(HEADER, 1)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id", is(itemDto.getId()), Long.class),
                        jsonPath("$.name", is(itemDto.getName())),
                        jsonPath("$.description", is(itemDto.getDescription())),
                        jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class)
                );
    }

    @Test
    void searchItems() throws Exception {
        List<ItemDto> itemList = List.of(itemDto);

        when(itemService.searchItems(anyLong(), anyString(), any())).thenReturn(itemList);

        mvc.perform(get("/items/search")
                        .header(HEADER, 1)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("text", "item")
                        .param("from", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemList)));
    }

    @Test
    void searchItemsWithoutText() throws Exception {
        mvc.perform(get("/items/search")
                        .header(HEADER, 1)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("from", "0")
                        .param("size", "2"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void saveNewComment() throws Exception {
        when(itemService.saveComment(anyLong(), anyLong(), any())).thenReturn(commentDto);

        mvc.perform(post("/items/1/comment")
                        .header(HEADER, 1)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id", is(commentDto.getId()), Long.class),
                        jsonPath("$.text", is(commentDto.getText())),
                        jsonPath("$.authorName", is(commentDto.getAuthorName())),
                        jsonPath("$.created",
                                is(commentDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                );
    }

    @Test
    void saveNewCommentWithoutText() throws Exception {
        CommentDto commentWithoutText = new CommentDto(
                1L,
                "",
                "user",
                LocalDateTime.now()
        );

        mvc.perform(post("/items/1/comment")
                        .header(HEADER, 1)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentWithoutText))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
