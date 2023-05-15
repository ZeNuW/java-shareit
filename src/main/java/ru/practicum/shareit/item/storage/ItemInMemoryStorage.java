package ru.practicum.shareit.item.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ObjectForbiddenException;
import ru.practicum.shareit.exception.ObjectNotExistException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ItemInMemoryStorage implements ItemStorage {

    private final Map<Long, Item> items = new HashMap<>();
    private long id;

    @Override
    public Item addItem(Item item) {
        item.setId(++id);
        items.put(id, item);
        log.info("Добавлен новый предмет " + item);
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        Item itemToUpdate = items.get(item.getId());
        if (itemToUpdate == null) {
            throw new ObjectNotExistException("Предмет с id: " + item.getId() + " не был найден");
        }
        if (!item.getOwner().equals(itemToUpdate.getOwner())) {
            throw new ObjectForbiddenException("Попытка изменить владельца предмета");
        }
        if (item.getDescription() != null) {
            itemToUpdate.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemToUpdate.setAvailable(item.getAvailable());
        }
        if (item.getName() != null) {
            itemToUpdate.setName(item.getName());
        }
        log.info("Обновлён предмет " + itemToUpdate);
        return itemToUpdate;
    }

    @Override
    public Item getItem(long itemId) {
        Item item = items.get(itemId);
        if (item == null) {
            throw new ObjectNotExistException("Предмета с id " + itemId + " не существует");
        }
        return items.get(itemId);
    }

    @Override
    public List<Item> getUserItems(long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner().equals(userId)).collect(Collectors.toList());
    }

    @Override
    public List<Item> searchItems(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        return items.values().stream()
                .filter(item -> (item.getDescription().toLowerCase().contains(text.toLowerCase()) ||
                        item.getName().toLowerCase().contains(text.toLowerCase())) && item.getAvailable().equals(true))
                .collect(Collectors.toList());
    }
}