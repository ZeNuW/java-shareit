package ru.practicum.shareit.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
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
}
