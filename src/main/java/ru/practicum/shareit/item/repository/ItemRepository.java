package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByOwnerId(Long ownerId);

    @Query("select it " +
            "from Item as it " +
            "where lower(it.name) like lower(concat('%', :search, '%')) " +
            "or lower(it.description) like lower(concat('%', :search, '%'))" +
            "and it.available = true")
    List<Item> searchByQuery(@Param("search") String text);
}
