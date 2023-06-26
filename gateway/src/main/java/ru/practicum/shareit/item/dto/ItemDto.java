package ru.practicum.shareit.item.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Builder
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ItemDto {
    private Long id;
    @NotBlank
    private String description;
    @NotBlank
    private String name;
    @NotNull
    private Boolean available;
    private Long requestId;
}