package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ObjectNotExistException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDto getUser(long userId) {
        return UserMapper.userToDto(userRepository.findById(userId).orElseThrow(
                () -> new ObjectNotExistException(String.format("Пользователь с id: %d не найден.", userId))));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAll() {
        return userRepository.findAll().stream().map(UserMapper::userToDto).collect(Collectors.toList());
    }

    @Override
    public UserDto add(UserDto userDto) {
        return UserMapper.userToDto(userRepository.save(UserMapper.userFromDto(userDto)));
    }

    @Override
    public UserDto update(UserDto userDto, long userId) {
        userRepository.updateUser(userId, userDto.getName(), userDto.getEmail());
        return UserMapper.userToDto(userRepository.getReferenceById(userId));
    }

    @Override
    public void delete(long userId) {
        userRepository.deleteById(userId);
    }
}