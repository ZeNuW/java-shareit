package ru.practicum.shareit.repository;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ItemRequestRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    EasyRandom easyRandom = new EasyRandom();

    @Test

    public void findAllByCreator_IdTest() {
        User owner = easyRandom.nextObject(User.class);
        owner.setId(null);
        User user = easyRandom.nextObject(User.class);
        user.setId(null);
        userRepository.save(owner);
        userRepository.save(user);
        Item item = easyRandom.nextObject(Item.class);
        item.setId(null);
        item.setAvailable(true);
        item.setOwner(owner);
        item.setItemRequest(null);
        Item item2 = easyRandom.nextObject(Item.class);
        item2.setId(null);
        item2.setAvailable(true);
        item2.setOwner(user);
        item2.setItemRequest(null);
        itemRepository.save(item);
        itemRepository.save(item2);
        ItemRequestDto itemRequest = new ItemRequestDto(null, "предмет", LocalDateTime.now(), null);
        ItemRequest ownerRequest = itemRequestRepository.save(ItemRequestMapper.itemRequestDtoToItemRequest(itemRequest, owner));
        ItemRequestDto itemRequest2 = new ItemRequestDto(null, "предмет2", LocalDateTime.now(), null);
        ItemRequest userRequest = itemRequestRepository.save(ItemRequestMapper.itemRequestDtoToItemRequest(itemRequest2, user));

        List<ItemRequest> ownerReqList = itemRequestRepository.findAllByCreator_Id(owner.getId());
        List<ItemRequest> userReqList = itemRequestRepository.findAllByCreator_Id(user.getId());

        assertThat(ownerReqList.get(0)).isEqualTo(ownerRequest);
        assertThat(userReqList.get(0)).isEqualTo(userRequest);
    }

    @Test
    public void findAllByCreator_IdNotTest() {
        User owner = easyRandom.nextObject(User.class);
        owner.setId(null);
        User user = easyRandom.nextObject(User.class);
        user.setId(null);
        userRepository.save(owner);
        userRepository.save(user);
        Item item = easyRandom.nextObject(Item.class);
        item.setId(null);
        item.setAvailable(true);
        item.setOwner(owner);
        item.setItemRequest(null);
        Item item2 = easyRandom.nextObject(Item.class);
        item2.setId(null);
        item2.setAvailable(true);
        item2.setOwner(user);
        item2.setItemRequest(null);
        itemRepository.save(item);
        itemRepository.save(item2);
        ItemRequestDto itemRequest = new ItemRequestDto(null, "предмет", LocalDateTime.now(), null);
        ItemRequest ownerRequest = itemRequestRepository.save(ItemRequestMapper.itemRequestDtoToItemRequest(itemRequest, owner));
        ItemRequestDto itemRequest2 = new ItemRequestDto(null, "предмет2", LocalDateTime.now(), null);
        ItemRequest userRequest = itemRequestRepository.save(ItemRequestMapper.itemRequestDtoToItemRequest(itemRequest2, user));

        List<ItemRequest> ownerReqList = itemRequestRepository.findAllByCreator_IdNot(owner.getId(), Pageable.ofSize(10)).stream().collect(Collectors.toList());
        List<ItemRequest> userReqList = itemRequestRepository.findAllByCreator_IdNot(user.getId(), Pageable.ofSize(10)).stream().collect(Collectors.toList());

        assertThat(ownerReqList.get(0)).isEqualTo(userRequest);
        assertThat(userReqList.get(0)).isEqualTo(ownerRequest);
    }
}
