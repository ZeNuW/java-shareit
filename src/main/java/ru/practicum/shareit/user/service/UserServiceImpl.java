package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ObjectNotExistException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public UserDto getUser(long userId) {
        return UserMapper.userToDto(userRepository.findById(userId).orElseThrow(
                () -> new ObjectNotExistException("Пользователь с id: " + userId + " не найден.")));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<UserDto> findAll() {
        return userRepository.findAll().stream().map(UserMapper::userToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public UserDto add(UserDto userDto) {
        System.out.println(userDto);
        return UserMapper.userToDto(userRepository.save(UserMapper.userFromDto(userDto)));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public UserDto update(UserDto userDto, long userId) {
        userRepository.updateUser(userId, userDto.getName(), userDto.getEmail());
        return UserMapper.userToDto(userRepository.getReferenceById(userId));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(long userId) {
        userRepository.deleteById(userId);
    }
}