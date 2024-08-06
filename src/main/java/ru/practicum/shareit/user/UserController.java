package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

import java.util.Collection;


@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto create(@Validated(Create.class) @RequestBody UserDto userDto) {
        log.info("POST /users request: {}", userDto);
        UserDto createdUser = userService.create(userDto);
        log.info("POST /users response: {}", createdUser);
        return createdUser;
    }

    @PatchMapping("/{userId}")
    public UserDto update(@Validated(Update.class) @RequestBody UserDto userDto, @PathVariable long userId) {
        log.info("PATCH /users/{} request: {}", userId, userDto);
        UserDto updatedUser = userService.update(userDto, userId);
        log.info("PATCH /users/{} response: {}", userId, updatedUser);
        return updatedUser;
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long userId) {
        log.info("DELETE /users/{} request", userId);
        userService.delete(userId);
        log.info("DELETE /users/{} response: success", userId);
    }

    @GetMapping("/{userId}")
    public UserDto getById(@PathVariable long userId) {
        log.info("GET /users/{} request", userId);
        UserDto user = userService.getById(userId);
        log.info("GET /users/{} response: {}", userId, user);
        return user;
    }

    @GetMapping
    public Collection<UserDto> getAll() {
        log.info("GET /users request");
        Collection<UserDto> users = userService.getAll();
        log.info("GET /users response: {}", users.size());
        return users;
    }
}
