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
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
@ExtendWith({MockitoExtension.class, SpringExtension.class})
public class ItemRequestControllerTest {

    private final LocalDateTime nowTime = LocalDateTime.now();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemRequestService itemRequestService;
    private final ItemRequestDto itemRequestDto = new ItemRequestDto(
            1L, "Test Description", nowTime, new ArrayList<>());
    private final ItemRequestDto itemRequestDto2 = new ItemRequestDto(
            2L, "Test Description2", nowTime, new ArrayList<>());

    @Test
    public void createItemRequestTest() throws Exception {
        when(itemRequestService.createItemRequest(any(ItemRequestDto.class), anyLong()))
                .thenReturn(itemRequestDto);
        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content("{\"name\":\"Дрель\",\"description\":\"Простая дрель\",\"available\":true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.created").value(nowTime.format(formatter)))
                .andExpect(jsonPath("$.items").value(new ArrayList<>()));
    }

    @Test
    public void getItemRequestByOwnerTest() throws Exception {
        List<ItemRequestDto> itemRequestList = Arrays.asList(itemRequestDto, itemRequestDto2);
        when(itemRequestService.getItemRequestByOwner(anyLong()))
                .thenReturn(itemRequestList);
        mockMvc.perform(get("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("Test Description"))
                .andExpect(jsonPath("$[0].created").value(nowTime.format(formatter)))
                .andExpect(jsonPath("$[0].items").value(new ArrayList<>()))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].description").value("Test Description2"))
                .andExpect(jsonPath("$[1].created").value(nowTime.format(formatter)))
                .andExpect(jsonPath("$[1].items").value(new ArrayList<>()));
    }

    @Test
    public void getAllItemRequestByPageTest() throws Exception {
        List<ItemRequestDto> itemRequestList = Arrays.asList(itemRequestDto, itemRequestDto2);
        when(itemRequestService.getAllItemRequestByPage(anyInt(), anyInt(), anyLong()))
                .thenReturn(itemRequestList);
        mockMvc.perform(get("/requests/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("from", "0")
                        .param("size", "10")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("Test Description"))
                .andExpect(jsonPath("$[0].created").value(nowTime.format(formatter)))
                .andExpect(jsonPath("$[0].items").value(new ArrayList<>()))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].description").value("Test Description2"))
                .andExpect(jsonPath("$[1].created").value(nowTime.format(formatter)))
                .andExpect(jsonPath("$[1].items").value(new ArrayList<>()));
    }

    @Test
    public void getItemRequestTest() throws Exception {
        when(itemRequestService.getItemRequest(anyLong(), anyLong()))
                .thenReturn(itemRequestDto);
        mockMvc.perform(get("/requests/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.created").value(nowTime.format(formatter)))
                .andExpect(jsonPath("$.items").value(new ArrayList<>()));
    }
}