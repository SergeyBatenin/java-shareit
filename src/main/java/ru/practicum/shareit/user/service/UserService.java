package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {
    UserDto create(UserDto user);

    UserDto update(UserDto user, long userId);

    void delete(long userId);

    UserDto getById(long userId);

    Collection<UserDto> getAll();
}
