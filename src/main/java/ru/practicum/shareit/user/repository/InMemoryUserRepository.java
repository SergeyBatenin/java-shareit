package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class InMemoryUserRepository {
    private static long identifier = 1;
    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();


    public User create(User user) {
        user.setId(identifier);
        users.put(identifier, user);
        identifier++;
        emails.add(user.getEmail());
        return user;
    }

    public User update(User user, long userId) {
        User updatedUser = users.get(userId);
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            emails.remove(updatedUser.getEmail());
            emails.add(user.getEmail());
            updatedUser.setEmail(user.getEmail());
        }
        if (user.getName() != null && !user.getName().isBlank()) {
            updatedUser.setName(user.getName());
        }

        return updatedUser;
    }

    public void delete(long userId) {
        User removedUser = users.remove(userId);
        if (removedUser != null) {
            emails.remove(removedUser.getEmail());
        }
    }

    public Optional<User> getById(long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    public boolean isEmailAvailability(String email) {
        return !emails.contains(email);
    }

    public Collection<User> getAll() {
        return List.copyOf(users.values());
    }
}
