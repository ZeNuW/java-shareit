package ru.practicum.shareit.user.dto;

import lombok.*;

@Builder
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class UserDto {
    private Long id;
    private String email;
    private String name;
}