package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ObjectAlreadyExistException;
import ru.practicum.shareit.exception.ObjectNotExistException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class UserInMemoryStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private long id;

    @Override
    public User getUser(long userId) {
        User user = users.get(userId);
        if (user == null) {
            throw new ObjectNotExistException("Пользователь с id: " + userId + " не найден");
        }
        return users.get(userId);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User add(User user) {
        boolean hasDuplicateEmail = users.values().stream()
                .anyMatch(user1 -> user1.getEmail().equals(user.getEmail()));
        if (hasDuplicateEmail) {
            throw new ObjectAlreadyExistException("Пользователь с email: " + user.getEmail() + " уже существует");
        }
        user.setId(++id);
        users.put(id, user);
        log.info("Добавлен новый пользователь " + user);
        return user;
    }

    @Override
    public User update(User user) {
        if (!users.containsKey(user.getId())) {
            throw new ObjectNotExistException("Пользователь с id: " + user.getId() + " не существует");
        }
        boolean hasDuplicateEmail = users.values().stream()
                .filter(user1 -> !user.getId().equals(user1.getId()))
                .anyMatch(user1 -> user1.getEmail().equals(user.getEmail()));
        if (hasDuplicateEmail) {
            throw new ObjectAlreadyExistException("Пользователь с email: " + user.getEmail() + " уже существует");
        }
        User userToUpdate = users.get(user.getId());
        if (userToUpdate == null) {
            throw new ObjectNotExistException("Пользователь с id: " + user.getId() + " не был найден");
        }
        if (user.getEmail() != null) {
            userToUpdate.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            userToUpdate.setName(user.getName());
        }
        log.info("Обновлён пользователь " + user);
        return userToUpdate;
    }

    @Override
    public void delete(long userId) {
        users.remove(userId);
    }
}