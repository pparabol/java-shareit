package ru.practicum.shareit.user.repository;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private static long nextId;

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User find(long id) {
        checkContains(id);
        return users.get(id);
    }

    @Override
    public User save(User user) {
        checkEmail(user.getEmail());
        user.setId(generateId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(long id, User user) {
        User created = users.get(id);
        if (!StringUtils.isBlank(user.getName())) {
            created.setName(user.getName());
        }
        String email = user.getEmail();
        if ((email != null) && (!created.getEmail().equals(email))) {
            checkEmail(email);
            created.setEmail(email);
        }
        users.put(id, created);
        return created;
    }

    @Override
    public void delete(long id) {
        checkContains(id);
        users.remove(id);
    }

    private void checkContains(long id) {
        if (!users.containsKey(id)) {
            throw new ValidationException(
                    HttpStatus.NOT_FOUND,
                    String.format("Пользователь с ID %d не найден", id)
            );
        }
    }

    private long generateId() {
        return ++nextId;
    }

    private void checkEmail(String email) {
        boolean isUnique = users.values().stream()
                .map(User::getEmail)
                .noneMatch(userEmail -> userEmail.equals(email));
        if (!isUnique) {
            throw new ValidationException(
                    HttpStatus.CONFLICT,
                    String.format("Пользователь с email '%s' уже существует", email)
            );
        }
    }
}
