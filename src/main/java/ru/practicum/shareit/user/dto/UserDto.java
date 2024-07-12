package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserDto {
    private String name;
    @Email
    private String email;
}
