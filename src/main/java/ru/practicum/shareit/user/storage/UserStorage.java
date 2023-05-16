package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {

    User getUser(long userId);

    List<User> findAll();

    User add(User user);

    User update(User user);

    void delete(long userId);
}
