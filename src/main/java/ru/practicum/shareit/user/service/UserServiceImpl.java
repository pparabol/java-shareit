package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
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
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .orElseThrow(
                        () -> new ValidationException(
                                HttpStatus.NOT_FOUND,
                                String.format("Пользователь с ID %d не найден", id))
                );
    }

    @Override
    @Transactional
    public UserDto saveUser(UserDto userDto) {
        User user = userRepository.save(userMapper.toEntity(userDto));
        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public UserDto updateUser(long id, UserDto userDto) {
        User created = userRepository.findById(id).orElseThrow(
                () -> new ValidationException(
                        HttpStatus.NOT_FOUND,
                        String.format("Пользователь с ID %d не найден", id))
        );
        if (!StringUtils.isBlank(userDto.getName())) {
            created.setName(userDto.getName());
        }
        String email = userDto.getEmail();
        if ((email != null) && (!created.getEmail().equals(email))) {
            created.setEmail(email);
        }
        return userMapper.toDto(userRepository.save(created));
    }

    @Override
    @Transactional
    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }
}
