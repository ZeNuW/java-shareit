package ru.practicum.shareit.user.dto;

import lombok.*;
import ru.practicum.shareit.valid.pointing.Create;
import ru.practicum.shareit.valid.pointing.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@Getter
@Setter
public class UserDto {
    private Long id;
    @Email(message = "Передан невалидный email", groups = {Create.class, Update.class})
    @NotBlank(groups = {Create.class})
    private String email;
    @NotBlank(groups = {Create.class})
    private String name;
}