package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
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
    private final UserMapper mapper;

    @Transactional
    public UserDto create(UserDto userDto) {
//        checkUserEmailAvailability(user.getEmail());
        User user = mapper.dtoToUser(userDto);
        return mapper.userToDto(userRepository.save(user));
    }

    @Transactional
    public UserDto update(UserDto userDto, long userId) {
        User updatedUser = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.debug("UPDATE USER By ID={}. Пользователь с айди {} не найден", userId, userId);
                    return new NotFoundException("Пользователь с id=" + userId + " не существует");
                });

        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
            updatedUser.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            updatedUser.setName(userDto.getName());
        }
        return mapper.userToDto(userRepository.save(updatedUser));
    }

    @Transactional
    public void delete(long userId) {
        userRepository.deleteById(userId);
    }

    @Transactional(readOnly = true)
    public UserDto getById(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.debug("GET USER By ID={}. Пользователь с айди {} не найден", userId, userId);
                    return new NotFoundException("Пользователь с id=" + userId + " не существует");
                });

        return mapper.userToDto(user);
    }

    @Transactional(readOnly = true)
    public Collection<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(mapper::userToDto)
                .collect(Collectors.toList());
    }
}
