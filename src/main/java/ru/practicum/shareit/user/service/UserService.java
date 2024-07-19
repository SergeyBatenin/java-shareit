package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {
    UserDto create(User user);

    UserDto update(User user, long userId);

    void delete(long userId);

    UserDto getById(long userId);

    Collection<UserDto> getAll();
}
