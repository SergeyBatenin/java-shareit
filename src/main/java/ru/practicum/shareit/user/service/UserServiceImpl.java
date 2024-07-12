package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailExistException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User create(User user) {
        checkUserEmailAvailability(user.getEmail());
        return userRepository.create(user);
    }

    @Override
    public User update(User user, long userId) {
        User updatedUser = userRepository.getById(userId)
                .orElseThrow(() -> {
                    log.debug("UPDATE USER By ID={}. Пользователь с айди {} не найден", userId, userId);
                    return new NotFoundException("Пользователь с id=" + userId + " не существует");
                });
        if (!updatedUser.getEmail().equals(user.getEmail())) {
            checkUserEmailAvailability(user.getEmail());
        }
        return userRepository.update(user, userId);
    }

    @Override
    public void delete(long userId) {
        userRepository.delete(userId);
    }

    @Override
    public User getById(long userId) {
        return userRepository.getById(userId)
                .orElseThrow(() -> {
                    log.debug("GET USER By ID={}. Пользователь с айди {} не найден", userId, userId);
                    return new NotFoundException("Пользователь с id=" + userId + " не существует");
                });
    }

    @Override
    public Collection<User> getAll() {
        return userRepository.getAll();
    }

    private void checkUserEmailAvailability(String email) {
        boolean emailAvailability = userRepository.isEmailAvailability(email);
        if (!emailAvailability) {
            log.debug("Электронный адрес '{}' уже зарегистрирован", email);
            throw new EmailExistException("Электронный адрес '" + email + "' уже зарегистрирован");
        }
    }
}
