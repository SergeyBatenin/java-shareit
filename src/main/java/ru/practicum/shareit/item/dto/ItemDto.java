package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ItemDto {
    private Long id;
    @NotBlank
    @Size(min = 1, max = 255)
    private String name;
    @NotBlank
    @Size(min = 1, max = 1000)
    private String description;
    @NotNull
    private Boolean available;
    private Long request;
}
