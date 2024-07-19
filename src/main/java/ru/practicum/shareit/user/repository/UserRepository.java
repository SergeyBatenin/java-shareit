package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.User;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository {
    User create(User user);

    User update(User user, long userId);

    void delete(long userId);

    Optional<User> getById(long userId);

    boolean isEmailAvailability(String email);

    Collection<User> getAll();
}
