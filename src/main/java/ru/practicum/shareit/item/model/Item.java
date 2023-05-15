package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Item {
    private Long id;
    private String description;
    private String name;
    private Boolean available;
    private Long owner;
}