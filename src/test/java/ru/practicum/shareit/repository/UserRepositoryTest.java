package ru.practicum.shareit.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private UserRepository userRepository;

    @Test
    public void testUpdateUser() {
        User user = new User(null, "john.doe@example.com", "John Doe");
        Assertions.assertNull(user.getId());
        entityManager.persist(user);
        assertNotNull(user.getId());
        String newName = "John Update";
        String newEmail = "john.update@example.com";
        userRepository.updateUser(user.getId(), newName, newEmail);
        entityManager.refresh(user);
        assertEquals(newName, user.getName());
        assertEquals(newEmail, user.getEmail());
    }
}