package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

@Data
@AllArgsConstructor
public class ItemDto {
    private Long id;
    @NotBlank(groups = {Create.class})
    @Size(min = 1, max = 255, groups = {Create.class, Update.class})
    private String name;
    @NotBlank(groups = {Create.class})
    @Size(min = 1, max = 1000, groups = {Create.class, Update.class})
    private String description;
    @NotNull(groups = {Create.class})
    private Boolean available;
    private Long request;
}
