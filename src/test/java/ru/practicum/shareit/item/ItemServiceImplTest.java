package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
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
public class ItemServiceImplTest {
    private final EntityManager em;
    private final UserService userService;
    private final ItemService itemService;
    private final ItemDto itemDto = ItemDto.builder()
            .name("item")
            .description("description")
            .available(true)
            .build();
    private UserDto createdUser;
    private ItemDto createdItem;

    @BeforeEach
    void setUp() {
        createdUser = userService.saveUser(new UserDto(1L, "name", "email@mail.ru"));
        createdItem = itemService.saveItem(createdUser.getId(), itemDto);
    }

    @Test
    void getItems() {
        List<ItemDto> actual = itemService.getItems(createdUser.getId(), PageRequest.of(0, 2));

        assertThat(actual).hasSize(1);
        assertThat(actual.get(0).getName()).isEqualTo(itemDto.getName());
        assertThat(actual.get(0).getDescription()).isEqualTo(itemDto.getDescription());
    }

    @Test
    void getItem() {
        ItemDto actual = itemService.getItem(createdUser.getId(), createdItem.getId());

        assertThat(actual.getName()).isEqualTo(itemDto.getName());
        assertThat(actual.getDescription()).isEqualTo(itemDto.getDescription());
        assertThat(actual.getAvailable()).isTrue();
    }

    @Test
    void getItemByIncorrectIdShouldThrowException() {
        assertThatThrownBy(
                () -> itemService.getItem(createdUser.getId(), 500)
        ).isInstanceOf(NotFoundException.class)
                .hasMessage("Вещь с ID 500 не найдена");
    }

    @Test
    void saveItem() {
        TypedQuery<Item> query = em.createQuery("from Item i where i.name = :name", Item.class);
        Item item = query.setParameter("name", itemDto.getName()).getSingleResult();

        assertThat(item.getId()).isNotNull();
        assertThat(item.getName()).isEqualTo(itemDto.getName());
        assertThat(item.getDescription()).isEqualTo(itemDto.getDescription());
        assertThat(item.getAvailable()).isTrue();
    }

    @Test
    void updateItem() {
        ItemDto itemWithNewDescription = ItemDto.builder()
                .id(createdItem.getId())
                .description("newDescription")
                .build();

        itemService.updateItem(createdUser.getId(), itemWithNewDescription);

        TypedQuery<Item> query = em.createQuery("from Item i where i.name = :name", Item.class);
        Item item = query.setParameter("name", itemDto.getName()).getSingleResult();

        assertThat(item.getId()).isNotNull();
        assertThat(item.getName()).isEqualTo(itemDto.getName());
        assertThat(item.getDescription()).isEqualTo(itemWithNewDescription.getDescription());
        assertThat(item.getAvailable()).isTrue();
    }

    @Test
    void updateItemByIncorrectUserIdShouldThrowException() {
        ItemDto item = ItemDto.builder()
                .id(createdItem.getId())
                .description("testUpdate")
                .build();
        assertThatThrownBy(
                () -> itemService.updateItem(30, item)
        ).isInstanceOf(NotFoundException.class)
                .hasMessage("Редактировать вещь может только её владелец");
    }
}
