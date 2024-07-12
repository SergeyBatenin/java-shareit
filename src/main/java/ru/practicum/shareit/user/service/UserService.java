package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;

import java.util.Collection;

public interface UserService {
    User create(User user);

    User update(User user, long userId);

    void delete(long userId);

    User getById(long userId);

    Collection<User> getAll();
}
