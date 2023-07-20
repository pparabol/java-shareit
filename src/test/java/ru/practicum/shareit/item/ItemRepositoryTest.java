package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRepositoryTest {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private Item item;

    @BeforeEach
    void setUp() {
        User user = userRepository.save(new User(1L, "name", "user@u.ru"));
        item = itemRepository.save(
                Item.builder()
                        .name("item")
                        .description("desc")
                        .available(true)
                        .owner(user)
                        .build()
        );
    }

    @Test
    void searchByQuery() {
        List<Item> result = itemRepository.searchByQuery("item", Pageable.ofSize(10)).toList();

        assertThat(result).hasSize(1);
        assertThat(result).contains(item);
    }

    @Test
    void searchByQueryShouldReturnEmptyResult() {
        List<Item> result = itemRepository.searchByQuery("query", Pageable.ofSize(10)).toList();

        assertThat(result).isEmpty();
    }
}
