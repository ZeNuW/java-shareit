package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserNotExistException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemDto addItem(long userId, ItemDto itemDto) {
        if (userStorage.getUser(userId) == null) {
            throw new UserNotExistException("Пользователя с id " + userId + " не существует.");
        }
        return ItemMapper.ItemToDto(itemStorage.addItem(ItemMapper.ItemFromDto(itemDto, userId)));
    }

    @Override
    public ItemDto updateItem(long userId, ItemDto itemDto, long itemId) {
        if (userStorage.getUser(userId) == null) {
            throw new UserNotExistException("Пользователя с id " + userId + " не существует.");
        }
        itemDto.setId(itemId);
        return ItemMapper.ItemToDto(itemStorage.updateItem(ItemMapper.ItemFromDto(itemDto, userId)));
    }

    @Override
    public ItemDto getItem(long itemId) {
        return ItemMapper.ItemToDto(itemStorage.getItem(itemId));
    }

    @Override
    public List<ItemDto> getUserItems(long userId) {
        return itemStorage.getUserItems(userId).stream()
                .map(ItemMapper::ItemToDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        return itemStorage.searchItems(text).stream()
                .map(ItemMapper::ItemToDto).collect(Collectors.toList());
    }
}