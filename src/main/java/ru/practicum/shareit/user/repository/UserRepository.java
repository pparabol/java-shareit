package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<User> findAll();

    Optional<User> find(long id);

    User save(User user);

    User update(long id, User user);

    void delete(long id);

    boolean isUnique(String email);
}
