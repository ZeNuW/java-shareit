package ru.practicum.shareit.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ObjectNotExistException;
import ru.practicum.shareit.exception.ObjectValidationException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class ItemRequestServiceIntegrationTest {

    @Autowired
    private ItemRequestService itemRequestService;
    @Autowired
    private UserRepository userRepository;

    @Test
    public void testCreateItemRequest() {
        User user = createUser();
        ItemRequestDto itemRequestDto = createItemRequestDto();
        ItemRequestDto createdRequest = itemRequestService.createItemRequest(itemRequestDto, user.getId());
        assertNotNull(createdRequest.getId());
        assertEquals(itemRequestDto.getDescription(), createdRequest.getDescription());
    }

    @Test
    public void testGetItemRequestByOwner() {
        User user = createUser();
        ItemRequestDto request1 = createItemRequest(user);
        ItemRequestDto request2 = createItemRequest(user);
        List<ItemRequestDto> requests = itemRequestService.getItemRequestByOwner(user.getId());
        assertEquals(2, requests.size());
        assertTrue(requests.contains(request1));
        assertTrue(requests.contains(request2));
    }

    @Test
    public void testGetAllItemRequestByPage() {
        User user1 = createUser();
        User user2 = createUser();
        ItemRequestDto request1 = createItemRequest(user1);
        ItemRequestDto request2 = createItemRequest(user2);
        List<ItemRequestDto> requests1 = itemRequestService.getAllItemRequestByPage(0, 10, user1.getId());
        assertEquals(1, requests1.size());
        assertTrue(requests1.contains(request2));
        List<ItemRequestDto> requests2 = itemRequestService.getAllItemRequestByPage(0, 10, user2.getId());
        assertEquals(1, requests2.size());
        assertTrue(requests2.contains(request1));
    }

    @Test
    public void testGetAllItemRequestByPageInvalidArguments() {
        User user = createUser();
        assertThrows(ObjectValidationException.class, () -> itemRequestService.getAllItemRequestByPage(-1, 10, user.getId()));
        assertThrows(ObjectValidationException.class, () -> itemRequestService.getAllItemRequestByPage(0, 0, user.getId()));
    }

    @Test
    public void testGetItemRequest() {
        User user = createUser();
        ItemRequestDto request = createItemRequest(user);
        ItemRequestDto retrievedRequest = itemRequestService.getItemRequest(user.getId(), request.getId());
        assertEquals(request, retrievedRequest);
    }

    @Test
    public void testGetItemRequestUserNotFound() {
        User user = createUser();
        ItemRequestDto request = createItemRequest(user);
        assertThrows(ObjectNotExistException.class, () -> itemRequestService.getItemRequest(999L, request.getId()));
    }

    @Test
    public void testGetItemRequestRequestNotFound() {
        User user = createUser();
        assertThrows(ObjectNotExistException.class, () -> itemRequestService.getItemRequest(user.getId(), 999L));
    }

    private User createUser() {
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        userRepository.save(user);
        return user;
    }

    private ItemRequestDto createItemRequestDto() {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("This is a test request.");
        return itemRequestDto;
    }

    private ItemRequestDto createItemRequest(User user) {
        ItemRequestDto itemRequestDto = createItemRequestDto();
        return itemRequestService.createItemRequest(itemRequestDto, user.getId());
    }
}
