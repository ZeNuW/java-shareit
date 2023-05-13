package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto getUser(long userId);

    List<UserDto> findAll();

    UserDto add(UserDto user);

    UserDto update(UserDto user, long userId);

    void delete(long userId);
}