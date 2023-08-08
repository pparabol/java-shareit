package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        log.info("Get all users");
        return userClient.getUsers();
    }

    @GetMapping("{id}")
    public ResponseEntity<Object> getUser(@PathVariable long id) {
        log.info("Get user userId={}", id);
        return userClient.getUser(id);
    }

    @PostMapping
    public ResponseEntity<Object> saveNewUser(@Valid @RequestBody UserDto userDto) {
        log.info("Creating user userDto={}", userDto);
        return userClient.saveNewUser(userDto);
    }

    @PatchMapping("{id}")
    public ResponseEntity<Object> updateUser(@PathVariable long id,
                                             @RequestBody UserDto userDto) {
        log.info("Updating user userId={}, userDto={}", id, userDto);
        return userClient.updateUser(id, userDto);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable long id) {
        log.info("Deleting user userId={}", id);
        return userClient.deleteUser(id);
    }
}
