package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwner_Id(Long userId);

    List<Item> findAllByDescriptionContainingIgnoreCaseAndAvailableIsTrue(String text);

    @Modifying
    @Query("UPDATE Item i SET i.description = COALESCE(:description, i.description), " +
            "i.available = COALESCE(:available, i.available), " +
            "i.name = COALESCE(:name, i.name) WHERE i.id = :itemId")
    void updateItem(@Param("itemId") long itemId, @Param("description") String description,
                    @Param("available") Boolean available, @Param("name") String name);
}