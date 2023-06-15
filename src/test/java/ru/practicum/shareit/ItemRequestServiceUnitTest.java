package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exception.ObjectNotExistException;
import ru.practicum.shareit.exception.ObjectValidationException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceUnitTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private final User user = new User(1L, "test@test.com", "name");
    private final ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "Test Description", LocalDateTime.now(), new ArrayList<>());
    private final ItemRequestDto itemRequestDtoSecond = new ItemRequestDto(2L, "Test Description2", LocalDateTime.now(), new ArrayList<>());

    @Test
    public void createItemRequestReturnsItemRequestDto() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(ItemRequestMapper.itemRequestDtoToItemRequest(itemRequestDto, user));
        ItemRequestDto result = itemRequestService.createItemRequest(itemRequestDto, user.getId());
        assertNotNull(result);
        assertEquals(itemRequestDto, result);
        verify(userRepository, times(1)).findById(user.getId());
        verify(itemRequestRepository, times(1)).save(any(ItemRequest.class));
    }

    @Test
    public void createItemRequestThrowObjectNotExistException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ObjectNotExistException.class,
                () -> itemRequestService.createItemRequest(itemRequestDto, user.getId()));
        verify(userRepository, times(1)).findById(user.getId());
        verify(itemRequestRepository, never()).save(any(ItemRequest.class));
    }

    @Test
    public void getItemRequestByOwnerReturnsItemRequestDto() {
        List<ItemRequest> itemRequests = new ArrayList<>();
        itemRequests.add(ItemRequestMapper.itemRequestDtoToItemRequest(itemRequestDto, user));
        itemRequests.add(ItemRequestMapper.itemRequestDtoToItemRequest(itemRequestDtoSecond, user));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByCreator_Id(anyLong())).thenReturn(itemRequests);
        List<ItemRequestDto> result = itemRequestService.getItemRequestByOwner(user.getId());
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(itemRequestDto, result.get(0));
        assertEquals(itemRequestDtoSecond, result.get(1));
        verify(userRepository, times(1)).findById(user.getId());
        verify(itemRequestRepository, times(1)).findAllByCreator_Id(user.getId());
    }

    @Test
    public void getItemRequestByOwnerThrowObjectNotExistException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ObjectNotExistException.class,
                () -> itemRequestService.getItemRequestByOwner(user.getId()));
        verify(userRepository, times(1)).findById(user.getId());
        verify(itemRequestRepository, never()).findAllByCreator_Id(user.getId());
    }

    @Test
    public void getAllItemRequestByPageReturnsItemRequestDto() {
        ArrayList<ItemRequest> itemRequests = new ArrayList<>();
        itemRequests.add(ItemRequestMapper.itemRequestDtoToItemRequest(itemRequestDto, user));
        itemRequests.add(ItemRequestMapper.itemRequestDtoToItemRequest(itemRequestDtoSecond, user));
        PageRequest pageRequest = PageRequest.of(0, 10);
        when(itemRequestRepository.findAllByCreator_IdNot(anyLong(), any(PageRequest.class))).thenReturn(new PageImpl<>(itemRequests));
        List<ItemRequestDto> result = itemRequestService.getAllItemRequestByPage(0, 10, user.getId());
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(itemRequestDto, result.get(0));
        assertEquals(itemRequestDtoSecond, result.get(1));
        verify(itemRequestRepository, times(1)).findAllByCreator_IdNot(user.getId(), pageRequest);
    }

    @Test
    public void getAllItemRequestByPageThrowObjectValidationException() {
        assertThrows(ObjectValidationException.class,
                () -> itemRequestService.getAllItemRequestByPage(-1, 0, user.getId()));
        verify(itemRequestRepository, never()).findAllByCreator_IdNot(anyLong(), any(PageRequest.class));
    }

    @Test
    public void getItemRequestReturnsItemRequestDto() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong())).thenReturn(
                Optional.of(ItemRequestMapper.itemRequestDtoToItemRequest(itemRequestDto, user)));
        ItemRequestDto result = itemRequestService.getItemRequest(user.getId(), itemRequestDto.getId());
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(itemRequestDto, result);
        verify(userRepository, times(1)).findById(user.getId());
        verify(itemRequestRepository, times(1)).findById(itemRequestDto.getId());
    }

    @Test
    public void getItemRequestThrowObjectNotExistException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ObjectNotExistException.class,
                () -> itemRequestService.getItemRequest(user.getId(), itemRequestDto.getId()));
        verify(userRepository, times(1)).findById(user.getId());
        verify(itemRequestRepository, never()).findById(itemRequestDto.getId());
    }

    @Test
    public void testGetItemRequest_ItemRequestNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ObjectNotExistException.class,
                () -> itemRequestService.getItemRequest(user.getId(), itemRequestDto.getId()));
        verify(userRepository, times(1)).findById(user.getId());
        verify(itemRequestRepository, times(1)).findById(itemRequestDto.getId());
    }
}
