package ru.practicum.shareit.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@Getter
@Setter
public class UserDto {
    private Long id;
    @Email(message = "Передан невалидный email")
    @NotBlank
    private String email;
    @NotBlank
    private String name;
}