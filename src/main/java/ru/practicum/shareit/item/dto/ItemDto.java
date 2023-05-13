package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
public class ItemDto {
    private Long id;
    @NotBlank
    private String description;
    @NotBlank
    private String name;
    @NotNull
    private Boolean available;
}