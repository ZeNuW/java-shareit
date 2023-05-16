package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {

    Item addItem(Item item);

    Item updateItem(Item item);

    Item getItem(long itemId);

    List<Item> getUserItems(long userId);

    List<Item> searchItems(String text);
}