package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("{id}")
    public UserDto getUser(@PathVariable long id) {
        return userService.getUser(id);
    }

    @GetMapping
    public List<UserDto> findAll() {
        return userService.findAll();
    }

    @PostMapping
    public UserDto add(@Valid @RequestBody UserDto userDto) {
        return userService.add(userDto);
    }

    @PatchMapping("{userId}")
    public UserDto update(@RequestBody UserDto userDto, @PathVariable long userId) {
        return userService.update(userDto, userId);
    }

    @DeleteMapping("{userId}")
    public void delete(@PathVariable long userId) {
        userService.delete(userId);
    }
}