package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImplTest {
    private final EntityManager em;
    private final UserService userService;
    private final UserDto userDto = new UserDto(1L, "name", "email@mail.ru");
    private UserDto created;

    @BeforeEach
    void setUp() {
        created = userService.saveUser(userDto);
    }

    @Test
    void getAllUsers() {
        List<UserDto> actual = userService.getAllUsers();

        assertThat(actual).isNotEmpty();
        assertThat(actual).hasSize(1);
        assertThat(actual.get(0)).isEqualTo(created);
    }

    @Test
    void getUser() {
        UserDto actual = userService.getUser(created.getId());

        assertThat(actual).isEqualTo(created);
    }

    @Test
    void getUserByIncorrectIdShouldThrowException() {
        assertThatThrownBy(
                () -> userService.getUser(404)
        ).isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь с ID 404 не найден");
    }

    @Test
    void saveUser() {
        TypedQuery<User> query = em.createQuery("from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail()).getSingleResult();

        assertThat(user.getId()).isNotNull();
        assertThat(user.getName()).isEqualTo(userDto.getName());
        assertThat(user.getEmail()).isEqualTo(userDto.getEmail());
    }

    @Test
    void saveUserWithSameEmailShouldThrowException() {
        UserDto userWithSameEmail = new UserDto(2L, "user", userDto.getEmail());

        assertThatThrownBy(
                () -> userService.saveUser(userWithSameEmail)
        ).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void updateUser() {
        UserDto dtoWithNewData = new UserDto(1L, "newName", "newEmail@mail.ru");

        userService.updateUser(created.getId(), dtoWithNewData);

        TypedQuery<User> query = em.createQuery("from User u where u.email = :email", User.class);
        User user = query.setParameter("email", dtoWithNewData.getEmail()).getSingleResult();

        assertThat(user.getId()).isNotNull();
        assertThat(user.getId()).isEqualTo(created.getId());
        assertThat(user.getName()).isEqualTo(dtoWithNewData.getName());
        assertThat(user.getEmail()).isEqualTo(dtoWithNewData.getEmail());
    }

    @Test
    void updateUserWithIncorrectIdShouldThrowException() {
        assertThatThrownBy(
                () -> userService.updateUser(404, userDto)
        ).isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь с ID 404 не найден");
    }

    @Test
    void deleteUser() {
        userService.deleteUser(created.getId());

        List<UserDto> actual = userService.getAllUsers();

        assertThat(actual).isEmpty();
    }
}
