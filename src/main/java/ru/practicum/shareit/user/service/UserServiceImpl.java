package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final Mapper<User, UserDto> userMapper;

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUser(long id) {
        return userRepository.find(id).map(userMapper::toDto).orElse(null);
    }

    @Override
    public UserDto saveUser(UserDto userDto) {
        checkEmail(userDto.getEmail());
        User user = userRepository.save(userMapper.toEntity(userDto));
        return userMapper.toDto(user);
    }

    @Override
    public UserDto updateUser(long id, UserDto userDto) {
        User created = userRepository.find(id).orElseThrow(
                () -> new ValidationException(
                        HttpStatus.NOT_FOUND,
                        String.format("Пользователь с ID %d не найден", id))
        );
        if (!StringUtils.isBlank(userDto.getName())) {
            created.setName(userDto.getName());
        }
        String email = userDto.getEmail();
        if ((email != null) && (!created.getEmail().equals(email))) {
            checkEmail(email);
            created.setEmail(email);
        }
        return userMapper.toDto(userRepository.update(id, created));
    }

    @Override
    public void deleteUser(long id) {
        userRepository.delete(id);
    }

    private void checkEmail(String email) {
        if (!userRepository.isUnique(email)) {
            throw new ValidationException(
                    HttpStatus.CONFLICT,
                    String.format("Пользователь с email '%s' уже существует", email)
            );
        }
    }
}
