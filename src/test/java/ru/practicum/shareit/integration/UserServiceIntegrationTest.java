package ru.practicum.shareit.integration;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.ObjectNotExistException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;
    private final EasyRandom easyRandom = new EasyRandom();

    @Test
    public void testGetUser() {
        UserDto expectedUser = createUser();
        UserDto actualUser = userService.getUser(expectedUser.getId());
        assertEquals(expectedUser, actualUser);
    }

    @Test
    public void testGetUserObjectNotExistException() {
        long nonExistUserId = 100L;
        assertThrows(ObjectNotExistException.class, () -> userService.getUser(nonExistUserId));
    }

    @Test
    public void testFindAll() {
        List<UserDto> expectedUsers = createUserList(3);
        List<UserDto> actualUsers = userService.findAll();
        assertEquals(expectedUsers.size(), actualUsers.size());
        assertTrue(actualUsers.containsAll(expectedUsers));
    }

    @Test
    public void testAdd() {
        UserDto userDto = createUserDto();
        UserDto addedUser = userService.add(userDto);
        assertNotNull(addedUser.getId());
        assertEquals(userDto.getName(), addedUser.getName());
        assertEquals(userDto.getEmail(), addedUser.getEmail());
    }


    @Test
    public void testUpdate() {
        UserDto existingUser = createUser();
        UserDto updatedUser = createUserDto();
        UserDto resultUser = userService.update(updatedUser, existingUser.getId());
        assertEquals(existingUser.getId(), resultUser.getId());
        assertEquals(updatedUser.getName(), resultUser.getName());
        assertEquals(updatedUser.getEmail(), resultUser.getEmail());
        UserDto userFromDatabase = userService.getUser(existingUser.getId());
        assertEquals(updatedUser.getName(), userFromDatabase.getName());
        assertEquals(updatedUser.getEmail(), userFromDatabase.getEmail());
    }

    @Test
    public void testDelete() {
        UserDto user = createUser();
        userService.delete(user.getId());
        assertThrows(ObjectNotExistException.class, () -> userService.getUser(user.getId()));
    }

    private UserDto createUserDto() {
        UserDto userDto = easyRandom.nextObject(UserDto.class);
        userDto.setId(null);
        return userDto;
    }

    private UserDto createUser() {
        return userService.add(createUserDto());
    }

    private List<UserDto> createUserList(int count) {
        return easyRandom.objects(UserDto.class, count).map(userService::add).collect(Collectors.toList());
    }
}
