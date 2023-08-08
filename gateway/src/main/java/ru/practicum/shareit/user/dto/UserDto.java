package ru.practicum.shareit.user.dto;

import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Value
public class UserDto {
    Long id;
    @NotBlank(message = "Имя не может быть пустым")
    String name;
    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Email введён некорректно")
    String email;
}
