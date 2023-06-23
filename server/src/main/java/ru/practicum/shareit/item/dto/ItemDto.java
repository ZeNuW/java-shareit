package ru.practicum.shareit.item.dto;

import lombok.*;

@Builder
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ItemDto {
    private Long id;
    private String description;
    private String name;
    private Boolean available;
    private Long requestId;
}