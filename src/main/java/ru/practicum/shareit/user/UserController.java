package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Запрос на получение всех пользователей");
        return userService.getAllUsers();
    }

    @GetMapping("{id}")
    public UserDto getUser(@PathVariable long id) {
        log.info("Запрос на получение пользователя с ID {}", id);
        return userService.getUser(id);
    }

    @PostMapping
    public UserDto saveNewUser(@Valid @RequestBody UserDto userDto) {
        log.info("Запрос на добавление пользователя: {}", userDto);
        return userService.saveUser(userDto);
    }

    @PatchMapping("{id}")
    public UserDto updateUser(@PathVariable long id,
                              @RequestBody UserDto userDto) {
        log.info("Запрос на редактирование пользователя: userId = {}, userDto = {}", id, userDto);
        return userService.updateUser(id, userDto);
    }

    @DeleteMapping("{id}")
    public void deleteUser(@PathVariable long id) {
        log.info("Запрос на удаление пользователя с ID {}", id);
        userService.deleteUser(id);
    }

}
