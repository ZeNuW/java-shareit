package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ObjectNotExistException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private final UserDto userDto = new UserDto(1L, "john.doe@mail.com", "John");

    @Test
    void getUserReturnsUserDto() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(UserMapper.userFromDto(userDto)));
        UserDto result = userService.getUser(userDto.getId());
        assertEquals(userDto, result);
        verify(userRepository, times(1)).findById(eq(userDto.getId()));
    }

    @Test
    void getUserThrowsObjectNotExistException() {
        when(userRepository.findById(anyLong())).thenThrow(ObjectNotExistException.class);
        assertThrows(ObjectNotExistException.class, () -> userService.getUser(userDto.getId()));
        verify(userRepository, times(1)).findById(eq(userDto.getId()));
    }

    @Test
    void findAllReturnsListOfUserDto() {
        List<UserDto> userDtoList = new ArrayList<>();
        userDtoList.add(userDto);
        userDtoList.add(new UserDto(userDto.getId() + 1, userDto.getEmail() + "test", userDto.getName() + "test"));
        when(userRepository.findAll()).thenReturn(userDtoList.stream().map(UserMapper::userFromDto).collect(Collectors.toList()));
        List<UserDto> result = userService.findAll();
        assertEquals(userDtoList, result);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void addReturnsUserDto() {
        when(userRepository.save(any(User.class))).thenReturn(UserMapper.userFromDto(userDto));
        UserDto result = userService.add(userDto);
        assertEquals(userDto, result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateReturnsUpdatedUserDto() {
        when(userRepository.getReferenceById(anyLong())).thenReturn(UserMapper.userFromDto(userDto));
        UserDto result = userService.update(userDto, userDto.getId());
        assertEquals(userDto, result);
        verify(userRepository, times(1)).updateUser(eq(userDto.getId()), eq(userDto.getName()), eq(userDto.getEmail()));
        verify(userRepository, times(1)).getReferenceById(eq(userDto.getId()));
    }

    @Test
    void deleteDeletesUser() {
        userService.delete(userDto.getId());
        verify(userRepository, times(1)).deleteById(eq(userDto.getId()));
    }
}