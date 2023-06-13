package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ObjectNotExistException;
import ru.practicum.shareit.exception.ObjectValidationException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ItemRequestDto createItemRequest(ItemRequestDto itemRequestDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotExistException("Пользователь с id " + userId + " не найден."));
        return ItemRequestMapper.itemRequestToItemRequestDto(
                itemRequestRepository.save(ItemRequestMapper.itemRequestDtoToItemRequest(itemRequestDto, user)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getItemRequestByOwner(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotExistException("Пользователь с id " + userId + " не найден."));
        return ItemRequestMapper.itemRequestToItemRequestDto(itemRequestRepository.findAllByCreator_Id(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getAllItemRequestByPage(Integer from, Integer size, Long userId) {
        if (from < 0 || size <= 0) {
            throw new ObjectValidationException("Значение size или from не могут быть отрицательными");
        }
        PageRequest page = PageRequest.of(from / size, size);
        return itemRequestRepository.findAllByCreator_IdNot(userId, page).getContent().stream()
                .map(ItemRequestMapper::itemRequestToItemRequestDto)
                .sorted(Comparator.comparing(ItemRequestDto::getCreated)).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestDto getItemRequest(Long userId, Long requestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotExistException("Пользователь с id " + userId + " не найден."));
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new ObjectNotExistException("Запрос с id " + requestId + " не найден."));
        return ItemRequestMapper.itemRequestToItemRequestDto(itemRequest);
    }
}