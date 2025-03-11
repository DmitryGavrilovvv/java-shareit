package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByOwnerId(long userId);

    @Query("""
            SELECT
                     i
            FROM
                     Item i
            WHERE
                     i.available = true
                 AND
                     (
                         i.name ILIKE %?1%
                      OR
                         i.description ILIKE %?1%
                     )
            """)
    List<Item> findByNameOrDescriptionContainingIgnoreCase(String searchText);

    List<Item> findAllByItemRequestIdIn(List<Long> requestIds);
}
