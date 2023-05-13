package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.UserAlreadyExistException;
import ru.practicum.shareit.exception.UserNotExistException;
import ru.practicum.shareit.exception.UserValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UserInMemoryStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private long id;

    @Override
    public User getUser(long userId) {
        if (users.containsKey(userId)) {
            return users.get(userId);
        } else {
            throw new UserAlreadyExistException("Пользователь с id: " + userId + " не найден");
        }
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
            throw new UserValidationException("Пользователь с email: " + user.getEmail() + " уже существует");
        }
        user.setId(++id);
        users.put(id, user);
        return user;
    }

    @Override
    public User update(User user) {
        // не очень хорошие решения по проверке, но т.к они временные и всё переедет в БД подумал, что так будет проще
        if (!users.containsKey(user.getId())) {
            throw new UserNotExistException("Пользователь с id: " + user.getId() + " не существует");
        }
        boolean hasDuplicateEmail = users.values().stream()
                .filter(user1 -> !user.getId().equals(user1.getId()))
                .anyMatch(user1 -> user1.getEmail().equals(user.getEmail()));
        if (hasDuplicateEmail) {
            throw new UserValidationException("Пользователь с email: " + user.getEmail() + " уже существует");
        }
        User userToUpdate = users.get(user.getId());
        if (user.getEmail() != null) {
            userToUpdate.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            userToUpdate.setName(user.getName());
        }
        return userToUpdate;
    }

    @Override
    public void delete(long userId) {
        users.remove(userId);
    }
}