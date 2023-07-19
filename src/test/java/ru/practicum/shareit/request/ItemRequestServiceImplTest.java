package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
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
public class ItemRequestServiceImplTest {
    private final EntityManager em;
    private final UserService userService;
    private final ItemRequestService itemRequestService;
    private final ItemRequestDto itemRequestDto = ItemRequestDto.builder()
            .description("requestDescription")
            .build();

    private UserDto createdUser;
    private ItemRequestDto createdRequest;

    @BeforeEach
    void setUp() {
        createdUser = userService.saveUser(new UserDto(1L, "name", "email@mail.ru"));
        createdRequest = itemRequestService.saveItemRequest(createdUser.getId(), itemRequestDto);
    }

    @Test
    void saveItemRequest() {
        TypedQuery<ItemRequest> query = em.createQuery(
                "from ItemRequest ir where ir.description = :description", ItemRequest.class
        );
        ItemRequest request = query.setParameter("description", itemRequestDto.getDescription())
                .getSingleResult();

        assertThat(request.getId()).isNotNull();
        assertThat(request.getDescription()).isEqualTo(itemRequestDto.getDescription());
        assertThat(request.getRequestor().getId()).isEqualTo(createdUser.getId());
    }

    @Test
    void saveItemRequestWithIncorrectUserIdShouldThrowException() {
        assertThatThrownBy(
                () -> itemRequestService.saveItemRequest(404, itemRequestDto)
        ).isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь с ID 404 не найден");
    }

    @Test
    void getAllByRequestor() {
        List<ItemRequestDto> actual = itemRequestService.getAllByRequestor(createdUser.getId());

        assertThat(actual).hasSize(1);
        assertThat(actual.get(0).getDescription()).isEqualTo(itemRequestDto.getDescription());
    }

    @Test
    void getAllByRequestorWithIncorrectUserIdShouldThrowException() {
        assertThatThrownBy(
                () -> itemRequestService.getAllByRequestor(404)
        ).isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь с ID 404 не найден");
    }

    @Test
    void getAll() {
        List<ItemRequestDto> actual = itemRequestService.getAll(
                createdUser.getId(), PageRequest.of(0, 2)
        );

        assertThat(actual).isEmpty();
    }

    @Test
    void getAllWithIncorrectUserIdShouldThrowException() {
        assertThatThrownBy(
                () -> itemRequestService.getAll(404, PageRequest.of(0, 2))
        ).isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь с ID 404 не найден");
    }

    @Test
    void getItemRequest() {
        ItemRequestDto actual = itemRequestService.getItemRequest(createdUser.getId(), createdRequest.getId());

        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getDescription()).isEqualTo(itemRequestDto.getDescription());
    }

    @Test
    void getItemRequestByIncorrectIdShouldThrowException() {
        assertThatThrownBy(
                () -> itemRequestService.getItemRequest(createdUser.getId(), 100)
        ).isInstanceOf(NotFoundException.class)
                .hasMessage("Запрос с ID 100 не найден");
    }
}
