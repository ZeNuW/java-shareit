package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
public class Item {
    private Long id;
    @NotBlank
    private String description;
    @NotBlank
    private String name;
    @NotNull
    private Boolean available;
    @NotNull
    private Long owner;
}