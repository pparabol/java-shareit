package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserRepository {
    List<User> findAll();
    User find(long id);
    User save(User user);
    User update(long id, User user);
    void delete(long id);
}
