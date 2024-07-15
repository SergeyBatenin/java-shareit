package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailExistException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto create(User user) {
        checkUserEmailAvailability(user.getEmail());
        return UserMapper.userToDto(userRepository.create(user));
    }

    @Override
    public UserDto update(User user, long userId) {
        User updatedUser = userRepository.getById(userId)
                .orElseThrow(() -> {
                    log.debug("UPDATE USER By ID={}. Пользователь с айди {} не найден", userId, userId);
                    return new NotFoundException("Пользователь с id=" + userId + " не существует");
                });
        if (!updatedUser.getEmail().equals(user.getEmail())) {
            checkUserEmailAvailability(user.getEmail());
        }
        return UserMapper.userToDto(userRepository.update(user, userId));
    }

    @Override
    public void delete(long userId) {
        userRepository.delete(userId);
    }

    @Override
    public UserDto getById(long userId) {
        User user = userRepository.getById(userId)
                .orElseThrow(() -> {
                    log.debug("GET USER By ID={}. Пользователь с айди {} не найден", userId, userId);
                    return new NotFoundException("Пользователь с id=" + userId + " не существует");
                });

        return UserMapper.userToDto(user);
    }

    @Override
    public Collection<UserDto> getAll() {
        return userRepository.getAll().stream()
                .map(UserMapper::userToDto)
                .collect(Collectors.toList());
    }

    private void checkUserEmailAvailability(String email) {
        boolean emailAvailability = userRepository.isEmailAvailability(email);
        if (!emailAvailability) {
            log.debug("Электронный адрес '{}' уже зарегистрирован", email);
            throw new EmailExistException("Электронный адрес '" + email + "' уже зарегистрирован");
        }
    }
}
