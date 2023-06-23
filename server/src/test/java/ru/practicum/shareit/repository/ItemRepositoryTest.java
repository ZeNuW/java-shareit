package ru.practicum.shareit.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ItemRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    public void testUpdateItem() {
        User user = new User(1L, "john.doe@example.com", "John Doe");
        userRepository.save(user);
        Item item = new Item();
        item.setName("Item 1");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(user);
        entityManager.persist(item);
        itemRepository.updateItem(item.getId(), "New description", false, "Updated name");
        entityManager.refresh(item);
        assertThat(item.getDescription()).isEqualTo("New description");
        assertThat(item.getAvailable()).isEqualTo(false);
        assertThat(item.getName()).isEqualTo("Updated name");
    }

    @Test
    public void findAllByOwner_IdTest() {
        User user = new User(1L, "john.doe@example.com", "John Doe");
        userRepository.save(user);
        Item item = new Item();
        item.setName("Item 1");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(user);
        Item item2 = new Item();
        item2.setName("Item 2");
        item2.setDescription("Description2");
        item2.setAvailable(false);
        item2.setOwner(user);
        entityManager.persist(item);
        entityManager.persist(item2);
        List<Item> items = itemRepository.findAllByOwner_Id(user.getId(), Pageable.ofSize(10));
        assertThat(items.size()).isEqualTo(2);
        assertThat(items.get(0)).isEqualTo(item);
        assertThat(items.get(1)).isEqualTo(item2);
    }

    @Test
    public void findAllByDescriptionContainingIgnoreCaseAndAvailableIsTrue() {
        User user = new User(1L, "john.doe@example.com", "John Doe");
        userRepository.save(user);
        Item item = new Item();
        item.setName("Item 1");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(user);
        Item item2 = new Item();
        item2.setName("Item 2");
        item2.setDescription("Description");
        item2.setAvailable(true);
        item2.setOwner(user);
        entityManager.persist(item);
        entityManager.persist(item2);
        List<Item> items = itemRepository
                .findAllByDescriptionContainingIgnoreCaseAndAvailableIsTrue("Description", Pageable.ofSize(10));
        assertThat(items.size()).isEqualTo(2);
        assertThat(items.get(0)).isEqualTo(item);
        assertThat(items.get(1)).isEqualTo(item2);
    }
}
