package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
public class UserDto {
    private Long id;
    @Email(message = "Передан невалидный email")
    @NotBlank
    private String email;
    @NotBlank
    private String name;
}