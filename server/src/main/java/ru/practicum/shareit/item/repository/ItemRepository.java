package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Page<Item> findByOwnerId(Long ownerId, Pageable pageable);

    @Query("from Item as it " +
            "where lower(it.name) like lower(concat('%', :text, '%')) " +
            "or lower(it.description) like lower(concat('%', :text, '%'))" +
            "and it.available = true")
    Page<Item> searchByQuery(String text, Pageable pageable);

    List<Item> findByRequestId(Long requestId);
}
