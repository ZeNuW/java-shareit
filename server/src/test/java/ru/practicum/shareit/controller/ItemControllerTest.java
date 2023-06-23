package ru.practicum.shareit.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookings;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
@ExtendWith({MockitoExtension.class, SpringExtension.class})
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemService itemService;
    private final ItemDto itemDto = new ItemDto(1L, "описание1", "имя1", true, 1L);
    private final ItemDto itemDto2 = new ItemDto(2L, "описание2", "имя2", true, 1L);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Test
    public void addItemTest() throws Exception {
        when(itemService.addItem(anyLong(), any(ItemDto.class)))
                .thenReturn(itemDto);
        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content("{\"name\":\"Дрель\",\"description\":\"Простая дрель\",\"available\":true}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("описание1"))
                .andExpect(jsonPath("$.name").value("имя1"))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.requestId").value(1));
    }

    @Test
    public void updateItemTest() throws Exception {
        when(itemService.updateItem(anyLong(), any(ItemDto.class), anyLong()))
                .thenReturn(itemDto);
        mockMvc.perform(patch("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content("{\"name\": \"Item 1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("описание1"))
                .andExpect(jsonPath("$.name").value("имя1"))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.requestId").value(1));
    }

    @Test
    public void getItemTest() throws Exception {
        ItemWithBookings itemWithBookings = new ItemWithBookings(1L, "описание", "имя",
                true, 1L, null, null, null);
        itemWithBookings.setId(1L);
        itemWithBookings.setName("Item 1");
        itemWithBookings.setDescription("item1descr");
        when(itemService.getItem(anyLong(), anyLong()))
                .thenReturn(itemWithBookings);

        mockMvc.perform(get("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Item 1"))
                .andExpect(jsonPath("$.description").value("item1descr"))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.description").value("item1descr"));
    }

    @Test
    public void getUserItemsTest() throws Exception {
        ItemWithBookings itemWithBookings1 = new ItemWithBookings(1L, "описание", "имя",
                true, 1L, null, null, null);
        itemWithBookings1.setId(1L);
        itemWithBookings1.setName("Item 1");
        itemWithBookings1.setDescription("Descr1");
        ItemWithBookings itemWithBookings2 = new ItemWithBookings(2L, "описание", "имя",
                true, 1L, null, null, null);
        itemWithBookings2.setId(2L);
        itemWithBookings2.setName("Item 2");
        itemWithBookings2.setDescription("Descr2");
        List<ItemWithBookings> itemList = Arrays.asList(itemWithBookings1, itemWithBookings2);

        when(itemService.getUserItems(anyLong(), anyInt(), anyInt()))
                .thenReturn(itemList);

        mockMvc.perform(get("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("Descr1"))
                .andExpect(jsonPath("$[0].name").value("Item 1"))
                .andExpect(jsonPath("$[0].available").value(true))
                .andExpect(jsonPath("$[0].requestId").value(1))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].description").value("Descr2"))
                .andExpect(jsonPath("$[1].name").value("Item 2"))
                .andExpect(jsonPath("$[1].available").value(true))
                .andExpect(jsonPath("$[1].requestId").value(1));
    }

    @Test
    public void searchItemsTest() throws Exception {
        List<ItemDto> itemList = Arrays.asList(itemDto, itemDto2);
        when(itemService.searchItems(anyString(), anyInt(), anyInt()))
                .thenReturn(itemList);
        mockMvc.perform(get("/items/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("text", "search text"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("имя1"))
                .andExpect(jsonPath("$[0].description").value("описание1"))
                .andExpect(jsonPath("$[0].available").value(true))
                .andExpect(jsonPath("$[0].requestId").value(1))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("имя2"))
                .andExpect(jsonPath("$[1].description").value("описание2"))
                .andExpect(jsonPath("$[1].available").value(true))
                .andExpect(jsonPath("$[1].requestId").value(1));
    }

    @Test
    public void addCommentTest() throws Exception {
        LocalDateTime nowTime = LocalDateTime.now();
        CommentDto commentDto = new CommentDto(1L, "текст", "name", nowTime);
        when(itemService.addComment(anyLong(), anyLong(), any(CommentDto.class)))
                .thenReturn(commentDto);

        mockMvc.perform(post("/items/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content("{\"text\":\"Comment for item 1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("текст"))
                .andExpect(jsonPath("$.authorName").value("name"))
                .andExpect(jsonPath("$.created").value(nowTime.format(formatter)));
    }
}