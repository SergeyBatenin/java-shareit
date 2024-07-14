package ru.practicum.shareit.user.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.user.User;

@UtilityClass
public class UserMapper {
    public static UserDto userToDto(User user) {
        return new UserDto(user.getName(), user.getEmail());
    }
}
