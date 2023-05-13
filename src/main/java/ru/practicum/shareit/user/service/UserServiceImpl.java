package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Override
    public UserDto getUser(long userId) {
        return UserMapper.UserToDto(userStorage.getUser(userId));
    }

    @Override
    public List<UserDto> findAll() {
        return userStorage.findAll().stream().map(UserMapper::UserToDto).collect(Collectors.toList());
    }

    @Override
    public UserDto add(UserDto userDto) {
        return UserMapper.UserToDto(userStorage.add(UserMapper.UserFromDto(userDto)));
    }

    @Override
    public UserDto update(UserDto userDto, long userId) {
        userDto.setId(userId);
        return UserMapper.UserToDto(userStorage.update(UserMapper.UserFromDto(userDto)));
    }

    @Override
    public void delete(long userId) {
        userStorage.delete(userId);
    }
}