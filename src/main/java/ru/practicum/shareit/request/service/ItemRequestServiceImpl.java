package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ObjectNotExistException;
import ru.practicum.shareit.exception.ObjectValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestDto createItemRequest(ItemRequestDto itemRequestDto, Long userId) {
        User user = checkUserExist(userId);
        return ItemRequestMapper.itemRequestToItemRequestDto(itemRequestRepository.save(
                ItemRequestMapper.itemRequestDtoToItemRequest(itemRequestDto, user)), new ArrayList<>());
    }

    @Override
    public List<ItemRequestDto> getItemRequestByOwner(Long userId) {
        checkUserExist(userId);
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByCreator_Id(userId);
        List<Long> itemRequestsIds = itemRequests.stream().map(ItemRequest::getId).collect(Collectors.toList());
        List<ItemDto> items = itemRepository.findAllByItemRequest_IdIn(itemRequestsIds).stream()
                .map(ItemMapper::itemToDto).collect(Collectors.toList());
        return ItemRequestMapper.itemRequestToItemRequestDto(itemRequests, items);
    }

    @Override
    public List<ItemRequestDto> getAllItemRequestByPage(Integer from, Integer size, Long userId) {
        if (from < 0 || size <= 0) {
            throw new ObjectValidationException("Значение size или from не могут быть отрицательными");
        }
        PageRequest page = PageRequest.of(from / size, size);
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByCreator_IdNot(userId, page).getContent();
        List<Long> itemRequestsIds = itemRequests.stream().map(ItemRequest::getId).collect(Collectors.toList());
        List<ItemDto> items = itemRepository.findAllByItemRequest_IdIn(itemRequestsIds).stream()
                .map(ItemMapper::itemToDto).collect(Collectors.toList());
        return ItemRequestMapper.itemRequestToItemRequestDto(itemRequests, items).stream()
                .sorted(Comparator.comparing(ItemRequestDto::getCreated)).collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getItemRequest(Long userId, Long requestId) {
        checkUserExist(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new ObjectNotExistException(String.format("Запрос с id %d не найден.", requestId)));
        List<ItemDto> items = itemRepository.findAllByItemRequest_IdIn(List.of(itemRequest.getId())).stream()
                .map(ItemMapper::itemToDto).collect(Collectors.toList());
        return ItemRequestMapper.itemRequestToItemRequestDto(itemRequest, items);
    }

    private User checkUserExist(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotExistException(String.format("Пользователь с id: %d не найден.", userId)));
    }
}